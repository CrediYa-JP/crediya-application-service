package co.com.crediya.app.usecase.loanapplication;

import co.com.crediya.app.model.common.PageRequest;
import co.com.crediya.app.model.common.PagedResult;
import co.com.crediya.app.model.exception.common.UnauthorizedOperationException;
import co.com.crediya.app.model.exception.loanapplication.UserNotFoundException;
import co.com.crediya.app.model.loanapplication.LoanApplication;
import co.com.crediya.app.model.loanapplication.factory.LoanApplicationFactory;
import co.com.crediya.app.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.app.model.loantype.LoanType;
import co.com.crediya.app.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.app.model.exception.loanapplication.InvalidLoanTypeException;
import co.com.crediya.app.model.user.User;
import co.com.crediya.app.model.user.gateways.AuthServiceGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

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
                .flatMap(loanApplicationRepository::save);
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
}
