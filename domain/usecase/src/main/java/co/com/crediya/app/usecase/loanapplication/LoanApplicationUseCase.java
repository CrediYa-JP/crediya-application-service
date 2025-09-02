package co.com.crediya.app.usecase.loanapplication;

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
}
