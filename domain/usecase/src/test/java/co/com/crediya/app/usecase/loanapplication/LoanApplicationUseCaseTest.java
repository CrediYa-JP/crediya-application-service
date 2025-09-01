package co.com.crediya.app.usecase.loanapplication;

import co.com.crediya.app.model.exception.loanapplication.InvalidLoanTypeException;
import co.com.crediya.app.model.exception.loanapplication.UserNotFoundException;
import co.com.crediya.app.model.loanapplication.LoanApplication;
import co.com.crediya.app.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.app.model.loantype.LoanType;
import co.com.crediya.app.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.app.model.user.User;
import co.com.crediya.app.model.user.gateways.AuthServiceGateway;
import co.com.crediya.app.usecase.util.TestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private AuthServiceGateway authServiceGateway;

    @InjectMocks
    private LoanApplicationUseCase loanApplicationUseCase;

    @Test
    @DisplayName("Should register loan application successfully when validations pass")
    void shouldRegisterLoanApplicationSuccessfully() {

        LoanApplication inputApplication = TestDataBuilder.buildValidLoanApplication();
        LoanType validLoanType = TestDataBuilder.buildValidLoanType();
        User validUser = TestDataBuilder.buildValidUser();
        LoanApplication savedApplication = TestDataBuilder.buildSavedLoanApplication();

        when(loanTypeRepository.findById(inputApplication.getLoanTypeId())).thenReturn(Mono.just(validLoanType));
        when(authServiceGateway.getUserByIdentityDocument(inputApplication.getUserEmail())).thenReturn(Mono.just(validUser));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(Mono.just(savedApplication));

        StepVerifier.create(loanApplicationUseCase.registerLoanApplication(inputApplication))
                .expectNext(savedApplication)
                .verifyComplete();

        verify(loanApplicationRepository, times(1)).save(any(LoanApplication.class));
    }

    @Test
    @DisplayName("Should throw InvalidLoanTypeException when loan type does not exist")
    void shouldThrowExceptionWhenLoanTypeNotExists() {

        LoanApplication inputApplication = TestDataBuilder.buildValidLoanApplication();
        User validUser = TestDataBuilder.buildValidUser();

        when(loanTypeRepository.findById(inputApplication.getLoanTypeId())).thenReturn(Mono.empty());
        when(authServiceGateway.getUserByIdentityDocument(inputApplication.getUserEmail())).thenReturn(Mono.just(validUser));

        StepVerifier.create(loanApplicationUseCase.registerLoanApplication(inputApplication))
                .expectError(InvalidLoanTypeException.class)
                .verify();

        verify(loanApplicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should propagate user validation error when user service fails")
    void shouldPropagateUserValidationError() {
        LoanApplication inputApplication = TestDataBuilder.buildValidLoanApplication();
        LoanType validLoanType = TestDataBuilder.buildValidLoanType();
        UserNotFoundException userError = new UserNotFoundException("User not found");

        when(loanTypeRepository.findById(inputApplication.getLoanTypeId())).thenReturn(Mono.just(validLoanType));
        when(authServiceGateway.getUserByIdentityDocument(inputApplication.getUserEmail())).thenReturn(Mono.error(userError));

        StepVerifier.create(loanApplicationUseCase.registerLoanApplication(inputApplication))
                .expectError(UserNotFoundException.class)
                .verify();

        verify(loanApplicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should propagate repository save error")
    void shouldPropagateRepositorySaveError() {

        LoanApplication inputApplication = TestDataBuilder.buildValidLoanApplication();
        LoanType validLoanType = TestDataBuilder.buildValidLoanType();
        User validUser = TestDataBuilder.buildValidUser();
        RuntimeException repositoryError = new RuntimeException("Database connection failed");

        when(loanTypeRepository.findById(inputApplication.getLoanTypeId())).thenReturn(Mono.just(validLoanType));
        when(authServiceGateway.getUserByIdentityDocument(inputApplication.getUserEmail())).thenReturn(Mono.just(validUser));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(Mono.error(repositoryError));

        StepVerifier.create(loanApplicationUseCase.registerLoanApplication(inputApplication))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle both validation failures simultaneously with Mono.zip")
    void shouldHandleBothValidationFailures() {

        LoanApplication inputApplication = TestDataBuilder.buildValidLoanApplication();
        InvalidLoanTypeException loanTypeError = new InvalidLoanTypeException();
        UserNotFoundException userError = new UserNotFoundException("User not found");

        when(loanTypeRepository.findById(inputApplication.getLoanTypeId())).thenReturn(Mono.error(loanTypeError));
        when(authServiceGateway.getUserByIdentityDocument(inputApplication.getUserEmail())).thenReturn(Mono.error(userError));

        StepVerifier.create(loanApplicationUseCase.registerLoanApplication(inputApplication))
                .expectError(InvalidLoanTypeException.class)
                .verify();

        verify(loanApplicationRepository, never()).save(any());
    }
}