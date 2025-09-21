package co.com.crediya.app.usecase.loanapplication;

import co.com.crediya.app.model.capacityevaluation.CapacityEvaluationMessage;
import co.com.crediya.app.model.capacityevaluation.CapacityEvaluationResponse;
import co.com.crediya.app.model.capacityevaluation.gateway.CapacityEvaluationGateway;
import co.com.crediya.app.model.capacityevaluation.gateway.CapacityResponseGateway;
import co.com.crediya.app.model.common.PageRequest;
import co.com.crediya.app.model.common.PagedResult;
import co.com.crediya.app.model.exception.common.UnauthorizedOperationException;
import co.com.crediya.app.model.exception.loanapplication.ApplicationNotFoundException;
import co.com.crediya.app.model.loanapplication.LoanApplication;
import co.com.crediya.app.model.loanapplication.factory.LoanApplicationFactory;
import co.com.crediya.app.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.app.model.loantype.LoanType;
import co.com.crediya.app.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.app.model.exception.loanapplication.InvalidLoanTypeException;
import co.com.crediya.app.model.notifications.gateways.NotificationGateway;
import co.com.crediya.app.model.state.enums.LoanApplicationState;
import co.com.crediya.app.model.user.User;
import co.com.crediya.app.model.user.gateways.AuthServiceGateway;
import co.com.crediya.app.model.utils.LoanCalculationUtil;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final AuthServiceGateway authServiceGateway;
    private final NotificationGateway notificationGateway;
    private final CapacityEvaluationGateway capacityEvaluationGateway;
    private final CapacityResponseGateway capacityResponseGateway;



    public Mono<LoanApplication> registerLoanApplication(LoanApplication loanApplication,
                                                         String authenticatedEmail) {
        return Mono.zip(
                        validateLoanType(loanApplication.getLoanTypeId()),
                        validateUser(loanApplication.getUserIdentityDocument())
                )
                .flatMap(tuple -> {
                    User user = tuple.getT2();
                    return validateUserOwnership(user.getEmail(), authenticatedEmail)
                            .thenReturn(tuple);
                })
                .map(tuple -> LoanApplicationFactory.createPendingApplication(loanApplication))
                .flatMap(loanApplicationRepository::save)
                .flatMap(this::processAutomaticValidation);
    }

    private Mono<Void> validateUserOwnership(String userEmail, String authenticatedEmail) {
        return userEmail.equals(authenticatedEmail)
                ? Mono.empty()
                : Mono.error(new UnauthorizedOperationException());
    }

    private Mono<LoanType> validateLoanType(Long loanTypeId) {
        return loanTypeRepository.findById(loanTypeId)
                .switchIfEmpty(Mono.error(new InvalidLoanTypeException()));
    }

    private Mono<User> validateUser(String identityDocument) {
        return authServiceGateway.getUserByIdentityDocument(identityDocument);
    }

    public Mono<PagedResult<EnrichedApplicationData>> getApplicationsForReview(PageRequest pageRequest, String statusFilter) {
        return loanApplicationRepository.findApplicationsForReview(pageRequest, statusFilter)
                .flatMap(this::enrichApplicationsWithExternalData);
    }

    // NUEVO MÉTODO - Lógica de validación automática
    private Mono<LoanApplication> processAutomaticValidation(LoanApplication savedApplication) {
        return loanTypeRepository.findById(savedApplication.getLoanTypeId())
                .filter(LoanType::getAutomaticValidation)
                .flatMap(loanType -> triggerCapacityEvaluation(savedApplication, loanType))
                .thenReturn(savedApplication);
    }
    private Mono<Void> triggerCapacityEvaluation(LoanApplication application, LoanType loanType) {
        return authServiceGateway.getUserByIdentityDocument(application.getUserIdentityDocument())
                .flatMap(user -> calculateCurrentMonthlyDebt(application.getUserIdentityDocument())
                        .map(currentDebt -> buildCapacityEvaluationMessage(application, loanType, user, currentDebt)))
                .flatMap(capacityEvaluationGateway::sendForEvaluation);
    }

    // NUEVO MÉTODO - Calcular deuda mensual actual (reutiliza lógica HU-4)
    private Mono<BigDecimal> calculateCurrentMonthlyDebt(String identityDocument) {
        return loanApplicationRepository.findApprovedApplicationsByUser(identityDocument)
                .flatMap(this::enrichWithLoanTypeData)
                .map(this::calculateMonthlyPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private Mono<EnrichedApplicationData> enrichWithLoanTypeData(LoanApplication application) {
        return loanTypeRepository.findById(application.getLoanTypeId())
                .map(loanType -> EnrichedApplicationData.builder()
                        .application(application)
                        .loanType(loanType)
                        .build());
    }


    private BigDecimal calculateMonthlyPayment(EnrichedApplicationData enrichedData) {
        return LoanCalculationUtil.calculateMonthlyPayment(
                enrichedData.getApplication().getAmount(),
                enrichedData.getLoanType().getInterestRate(),
                enrichedData.getApplication().getTerm());
    }
    private CapacityEvaluationMessage buildCapacityEvaluationMessage(LoanApplication application,
                                                                     LoanType loanType,
                                                                     User user,
                                                                     BigDecimal currentDebt) {
        return CapacityEvaluationMessage.builder()
                .applicationId(application.getApplicationId())
                .userIdentityDocument(application.getUserIdentityDocument())
                .userBaseSalary(user.getBaseSalary())
                .currentMonthlyDebt(currentDebt)
                .newLoanAmount(application.getAmount())
                .newLoanTerm(application.getTerm())
                .newLoanInterestRate(loanType.getInterestRate())
                .build();
    }

    private Mono<PagedResult<EnrichedApplicationData>> enrichApplicationsWithExternalData(PagedResult<LoanApplication> pagedApplications) {
        List<LoanApplication> applications = pagedApplications.getContent();

        return Mono.zip(
                        fetchUsersData(applications),
                        fetchLoanTypesData(applications)
                )
                .map(tuple -> {
                    Map<String, User> usersMap = tuple.getT1();
                    Map<Long, LoanType> loanTypesMap = tuple.getT2();

                    List<EnrichedApplicationData> enrichedApplications = applications.stream()
                            .map(app -> enrichSingleApplication(app, usersMap, loanTypesMap))
                            .filter(enrichedApplicationData -> enrichedApplicationData.getUser() != null
                                    && enrichedApplicationData.getLoanType() != null)
                            .collect(Collectors.toList());

                    return PagedResult.of(
                            enrichedApplications,
                            pagedApplications.getPageNumber(),
                            pagedApplications.getPageSize(),
                            pagedApplications.getTotalElements()
                    );
                });
    }

    private Mono<Map<String, User>> fetchUsersData(List<LoanApplication> applications) {
        List<String> uniqueIdentityDocuments = applications.stream()
                .map(LoanApplication::getUserIdentityDocument)
                .distinct()
                .collect(Collectors.toList());

        return authServiceGateway.getUsersByIdentityDocuments(uniqueIdentityDocuments)
                .map(users -> users.stream()
                        .collect(Collectors.toMap(User::getIdentityDocument, Function.identity())));
    }

    private Mono<Map<Long, LoanType>> fetchLoanTypesData(List<LoanApplication> applications) {
        List<Long> uniqueLoanTypeIds = applications.stream()
                .map(LoanApplication::getLoanTypeId)
                .distinct()
                .collect(Collectors.toList());

        return loanTypeRepository.findByIds(uniqueLoanTypeIds)
                .map(loanTypes -> loanTypes.stream()
                        .collect(Collectors.toMap(LoanType::getLoanTypeId, Function.identity())));
    }

    private EnrichedApplicationData enrichSingleApplication(LoanApplication application,
                                                            Map<String, User> usersMap,
                                                            Map<Long, LoanType> loanTypesMap) {
        User user = usersMap.get(application.getUserIdentityDocument());
        LoanType loanType = loanTypesMap.get(application.getLoanTypeId());

        return EnrichedApplicationData.builder()
                .application(application)
                .user(user)
                .loanType(loanType)
                .build();
    }


    public Mono<LoanApplication> updateApplicationStatus(Long applicationId, LoanApplicationState newStatus) {
        return loanApplicationRepository.findById(applicationId)
                .switchIfEmpty(Mono.error(new ApplicationNotFoundException(applicationId)))
                .flatMap(application -> {
                    LoanApplication updatedApplication = application.toBuilder()
                            .stateId(newStatus.getId())
                            .lastModificationDate(LocalDateTime.now())
                            .build();

                    return loanApplicationRepository.save(updatedApplication)
                            .flatMap(savedApp -> sendNotificationIfNeeded(savedApp, newStatus)
                                    .then(Mono.just(savedApp)));
                });
    }

    private Mono<Void> sendNotificationIfNeeded(LoanApplication application, LoanApplicationState status) {
        return Mono.just(status)
                .filter(s -> s == LoanApplicationState.APPROVED || s == LoanApplicationState.REJECTED)
                .flatMap(s -> authServiceGateway.getUserByIdentityDocument(application.getUserIdentityDocument())
                        .flatMap(user -> notificationGateway.sendApplicationStatusNotification(
                                application.getApplicationId(),
                                user.getEmail(),
                                user.getFirstName() + " " + user.getLastName(),
                                status.name(),
                                application.getAmount(),
                                application.getTerm()
                        )))
                .onErrorMap(e -> new RuntimeException("Failed to send notification for applicationId=" + application.getApplicationId(), e))
                .then();
    }



    // This is a polling strategie for the last lamda response
    public Mono<Void> processCapacityResponses() {
        return capacityResponseGateway.pollForResponses()
                .flatMap(this::processResponse)
                .then();
    }

    private Mono<Void> processResponse(CapacityEvaluationResponse response) {
        return updateApplicationState(response)
                .flatMap(application -> sendNotificationIfNeeded(response))
                .then();
    }

    private Mono<LoanApplication> updateApplicationState(CapacityEvaluationResponse response) {
        Long newStateId = mapDecisionToStateId(response.getDecision());

        return loanApplicationRepository.findById(response.getApplicationId())
                .map(application -> application.toBuilder().stateId(newStateId).build())
                .flatMap(loanApplicationRepository::save);
    }

    private Mono<Void> sendNotificationIfNeeded(CapacityEvaluationResponse response) {
        if ("APPROVED".equals(response.getDecision()) || "REJECTED".equals(response.getDecision())) {
            return loanApplicationRepository.findById(response.getApplicationId())
                    .flatMap(application -> authServiceGateway.getUserByIdentityDocument(application.getUserIdentityDocument()))
                    .flatMap(user -> notificationGateway.sendApplicationStatusWithPaymentPlan(
                            response.getApplicationId(),
                            user.getEmail(),
                            user.getFirstName() + " " + user.getLastName(),
                            response.getDecision(),
                            response.getNewLoanPayment(),
                            null,
                            response.getPaymentPlan()
                    ));
        }
        return Mono.empty();
    }

    private Long mapDecisionToStateId(String decision) {
        return switch (decision) {
            case "APPROVED" -> LoanApplicationState.APPROVED.getId();
            case "REJECTED" -> LoanApplicationState.REJECTED.getId();
            case "MANUAL_REVIEW" -> LoanApplicationState.MANUAL_REVIEW.getId();
            default -> LoanApplicationState.PENDING_REVIEW.getId();
        };
    }
}
