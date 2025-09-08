package co.com.crediya.app.usecase.loanapplication;

import co.com.crediya.app.model.common.PageRequest;
import co.com.crediya.app.model.common.PagedResult;
import co.com.crediya.app.model.exception.common.UnauthorizedOperationException;
import co.com.crediya.app.model.exception.loanapplication.InvalidLoanTypeException;
import co.com.crediya.app.model.exception.loanapplication.UserNotFoundException;
import co.com.crediya.app.model.loanapplication.LoanApplication;
import co.com.crediya.app.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.app.model.loantype.LoanType;
import co.com.crediya.app.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.app.model.user.User;
import co.com.crediya.app.model.user.gateways.AuthServiceGateway;
import co.com.crediya.app.usecase.TestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoanApplicationUseCase Tests")
class LoanApplicationUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private AuthServiceGateway authServiceGateway;

    @InjectMocks
    private LoanApplicationUseCase loanApplicationUseCase;

    @Nested
    @DisplayName("Register Loan Application Tests")
    class RegisterLoanApplicationTests {

        @Test
        @DisplayName("Should register loan application successfully when all validations pass")
        void shouldRegisterLoanApplicationSuccessfully() {
            // Given
            LoanApplication inputApplication = TestDataBuilder.buildValidLoanApplication();
            LoanType validLoanType = TestDataBuilder.buildValidLoanType();
            User validUser = TestDataBuilder.buildValidUser();
            LoanApplication savedApplication = TestDataBuilder.buildSavedLoanApplication();
            String authenticatedEmail = TestDataBuilder.VALID_AUTHENTICATED_EMAIL;

            when(loanTypeRepository.findById(inputApplication.getLoanTypeId()))
                    .thenReturn(Mono.just(validLoanType));
            when(authServiceGateway.getUserByIdentityDocument(inputApplication.getUserIdentityDocument()))
                    .thenReturn(Mono.just(validUser));
            when(loanApplicationRepository.save(any(LoanApplication.class)))
                    .thenReturn(Mono.just(savedApplication));

            // When & Then
            StepVerifier.create(loanApplicationUseCase.registerLoanApplication(inputApplication, authenticatedEmail))
                    .expectNext(savedApplication)
                    .verifyComplete();

            // Verify interactions
            verify(loanTypeRepository).findById(inputApplication.getLoanTypeId());
            verify(authServiceGateway).getUserByIdentityDocument(inputApplication.getUserIdentityDocument());
            verify(loanApplicationRepository).save(any(LoanApplication.class));
        }

        @Test
        @DisplayName("Should throw InvalidLoanTypeException when loan type does not exist")
        void shouldThrowExceptionWhenLoanTypeNotExists() {
            // Given
            LoanApplication inputApplication = TestDataBuilder.buildValidLoanApplication();
            User validUser = TestDataBuilder.buildValidUser();
            String authenticatedEmail = TestDataBuilder.VALID_AUTHENTICATED_EMAIL;

            when(loanTypeRepository.findById(inputApplication.getLoanTypeId()))
                    .thenReturn(Mono.empty());
            when(authServiceGateway.getUserByIdentityDocument(inputApplication.getUserIdentityDocument()))
                    .thenReturn(Mono.just(validUser));

            // When & Then
            StepVerifier.create(loanApplicationUseCase.registerLoanApplication(inputApplication, authenticatedEmail))
                    .expectError(InvalidLoanTypeException.class)
                    .verify();

            verify(loanApplicationRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void shouldThrowExceptionWhenUserNotExists() {
            // Given
            LoanApplication inputApplication = TestDataBuilder.buildValidLoanApplication();
            LoanType validLoanType = TestDataBuilder.buildValidLoanType();
            String authenticatedEmail = TestDataBuilder.VALID_AUTHENTICATED_EMAIL;

            when(loanTypeRepository.findById(inputApplication.getLoanTypeId()))
                    .thenReturn(Mono.just(validLoanType));
            when(authServiceGateway.getUserByIdentityDocument(inputApplication.getUserIdentityDocument()))
                    .thenReturn(Mono.error(new UserNotFoundException()));

            // When & Then
            StepVerifier.create(loanApplicationUseCase.registerLoanApplication(inputApplication, authenticatedEmail))
                    .expectError(UserNotFoundException.class)
                    .verify();

            verify(loanApplicationRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw UnauthorizedOperationException when user emails don't match")
        void shouldThrowExceptionWhenUserEmailsDontMatch() {
            // Given
            LoanApplication inputApplication = TestDataBuilder.buildValidLoanApplication();
            LoanType validLoanType = TestDataBuilder.buildValidLoanType();
            User validUser = TestDataBuilder.buildValidUser(); // email: juan@test.com
            String authenticatedEmail = TestDataBuilder.DIFFERENT_AUTHENTICATED_EMAIL; // maria@test.com

            when(loanTypeRepository.findById(inputApplication.getLoanTypeId()))
                    .thenReturn(Mono.just(validLoanType));
            when(authServiceGateway.getUserByIdentityDocument(inputApplication.getUserIdentityDocument()))
                    .thenReturn(Mono.just(validUser));

            // When & Then
            StepVerifier.create(loanApplicationUseCase.registerLoanApplication(inputApplication, authenticatedEmail))
                    .expectError(UnauthorizedOperationException.class)
                    .verify();

            verify(loanApplicationRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should handle both validation failures simultaneously with Mono.zip")
        void shouldHandleBothValidationFailures() {
            // Given
            LoanApplication inputApplication = TestDataBuilder.buildValidLoanApplication();
            String authenticatedEmail = TestDataBuilder.VALID_AUTHENTICATED_EMAIL;

            when(loanTypeRepository.findById(inputApplication.getLoanTypeId()))
                    .thenReturn(Mono.error(new InvalidLoanTypeException()));
            when(authServiceGateway.getUserByIdentityDocument(inputApplication.getUserIdentityDocument()))
                    .thenReturn(Mono.error(new UserNotFoundException()));

            // When & Then - Should fail fast with first error (loan type validation)
            StepVerifier.create(loanApplicationUseCase.registerLoanApplication(inputApplication, authenticatedEmail))
                    .expectError(InvalidLoanTypeException.class)
                    .verify();

            verify(loanApplicationRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should propagate repository save error")
        void shouldPropagateRepositorySaveError() {
            // Given
            LoanApplication inputApplication = TestDataBuilder.buildValidLoanApplication();
            LoanType validLoanType = TestDataBuilder.buildValidLoanType();
            User validUser = TestDataBuilder.buildValidUser();
            String authenticatedEmail = TestDataBuilder.VALID_AUTHENTICATED_EMAIL;
            RuntimeException repositoryError = TestDataBuilder.buildRepositoryException();

            when(loanTypeRepository.findById(inputApplication.getLoanTypeId()))
                    .thenReturn(Mono.just(validLoanType));
            when(authServiceGateway.getUserByIdentityDocument(inputApplication.getUserIdentityDocument()))
                    .thenReturn(Mono.just(validUser));
            when(loanApplicationRepository.save(any(LoanApplication.class)))
                    .thenReturn(Mono.error(repositoryError));

            // When & Then
            StepVerifier.create(loanApplicationUseCase.registerLoanApplication(inputApplication, authenticatedEmail))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                            throwable.getMessage().equals("Database connection failed"))
                    .verify();
        }

        @Test
        @DisplayName("Should propagate external service error from auth gateway")
        void shouldPropagateExternalServiceError() {
            // Given
            LoanApplication inputApplication = TestDataBuilder.buildValidLoanApplication();
            LoanType validLoanType = TestDataBuilder.buildValidLoanType();
            String authenticatedEmail = TestDataBuilder.VALID_AUTHENTICATED_EMAIL;
            RuntimeException externalServiceError = TestDataBuilder.buildExternalServiceException();

            when(loanTypeRepository.findById(inputApplication.getLoanTypeId()))
                    .thenReturn(Mono.just(validLoanType));
            when(authServiceGateway.getUserByIdentityDocument(inputApplication.getUserIdentityDocument()))
                    .thenReturn(Mono.error(externalServiceError));

            // When & Then
            StepVerifier.create(loanApplicationUseCase.registerLoanApplication(inputApplication, authenticatedEmail))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                            throwable.getMessage().equals("Authentication service unavailable"))
                    .verify();
        }
    }

    @Nested
    @DisplayName("Get Applications For Review Tests")
    class GetApplicationsForReviewTests {

        @Test
        @DisplayName("Should return enriched applications successfully")
        void shouldReturnEnrichedApplicationsSuccessfully() {
            // Given
            PageRequest pageRequest = PageRequest.of(0, 10);
            String statusFilter = "pending";
            PagedResult<LoanApplication> pagedApplications = TestDataBuilder.buildPagedLoanApplications();
            List<User> users = TestDataBuilder.buildUsersList();
            List<LoanType> loanTypes = TestDataBuilder.buildLoanTypesList();

            when(loanApplicationRepository.findApplicationsForReview(pageRequest, statusFilter))
                    .thenReturn(Mono.just(pagedApplications));
            when(authServiceGateway.getUsersByIdentityDocuments(anyList()))
                    .thenReturn(Mono.just(users));
            when(loanTypeRepository.findByIds(anyList()))
                    .thenReturn(Mono.just(loanTypes));

            // When & Then
            StepVerifier.create(loanApplicationUseCase.getApplicationsForReview(pageRequest, statusFilter))
                    .expectNextMatches(result -> {
                        // Verify paging info
                        assert result.getPageNumber() == 0;
                        assert result.getPageSize() == 10;
                        assert result.getTotalElements() == 2L;

                        // Verify enriched data
                        assert result.getContent().size() == 2;

                        EnrichedApplicationData firstEnriched = result.getContent().get(0);
                        assert firstEnriched.getUser() != null;
                        assert firstEnriched.getLoanType() != null;
                        assert firstEnriched.getApplication() != null;

                        return true;
                    })
                    .verifyComplete();

            // Verify interactions
            verify(loanApplicationRepository).findApplicationsForReview(pageRequest, statusFilter);
            verify(authServiceGateway).getUsersByIdentityDocuments(anyList());
            verify(loanTypeRepository).findByIds(anyList());
        }


        @Test
        @DisplayName("Should filter out applications with missing user data")
        void shouldFilterOutApplicationsWithMissingUserData() {
            // Given
            PageRequest pageRequest = PageRequest.of(0, 10);
            PagedResult<LoanApplication> pagedApplications = TestDataBuilder.buildPagedLoanApplications();

            // Only return one user (missing second user)
            List<User> incompleteUsers = List.of(TestDataBuilder.buildValidUser());
            List<LoanType> loanTypes = TestDataBuilder.buildLoanTypesList();

            when(loanApplicationRepository.findApplicationsForReview(pageRequest, null))
                    .thenReturn(Mono.just(pagedApplications));
            when(authServiceGateway.getUsersByIdentityDocuments(anyList()))
                    .thenReturn(Mono.just(incompleteUsers));
            when(loanTypeRepository.findByIds(anyList()))
                    .thenReturn(Mono.just(loanTypes));

            // When & Then
            StepVerifier.create(loanApplicationUseCase.getApplicationsForReview(pageRequest, null))
                    .expectNextMatches(result -> {
                        // Should filter out application with missing user
                        assert result.getContent().size() == 1;

                        EnrichedApplicationData enrichedApp = result.getContent().get(0);
                        assert enrichedApp.getUser().getIdentityDocument().equals("12345678");

                        return true;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should filter out applications with missing loan type data")
        void shouldFilterOutApplicationsWithMissingLoanTypeData() {
            // Given
            PageRequest pageRequest = PageRequest.of(0, 10);
            PagedResult<LoanApplication> pagedApplications = TestDataBuilder.buildPagedLoanApplications();
            List<User> users = TestDataBuilder.buildUsersList();

            // Return empty loan types list
            List<LoanType> emptyLoanTypes = List.of();

            when(loanApplicationRepository.findApplicationsForReview(pageRequest, null))
                    .thenReturn(Mono.just(pagedApplications));
            when(authServiceGateway.getUsersByIdentityDocuments(anyList()))
                    .thenReturn(Mono.just(users));
            when(loanTypeRepository.findByIds(anyList()))
                    .thenReturn(Mono.just(emptyLoanTypes));

            // When & Then
            StepVerifier.create(loanApplicationUseCase.getApplicationsForReview(pageRequest, null))
                    .expectNextMatches(result -> {
                        // Should filter out all applications due to missing loan types
                        assert result.getContent().isEmpty();
                        return true;
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should propagate repository error")
        void shouldPropagateRepositoryError() {
            // Given
            PageRequest pageRequest = PageRequest.of(0, 10);
            RuntimeException repositoryError = TestDataBuilder.buildRepositoryException();

            when(loanApplicationRepository.findApplicationsForReview(pageRequest, null))
                    .thenReturn(Mono.error(repositoryError));

            // When & Then
            StepVerifier.create(loanApplicationUseCase.getApplicationsForReview(pageRequest, null))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                            throwable.getMessage().equals("Database connection failed"))
                    .verify();
        }

        @Test
        @DisplayName("Should propagate loan type service error")
        void shouldPropagateLoanTypeServiceError() {
            // Given
            PageRequest pageRequest = PageRequest.of(0, 10);
            PagedResult<LoanApplication> pagedApplications = TestDataBuilder.buildPagedLoanApplications();
            List<User> users = TestDataBuilder.buildUsersList();
            RuntimeException loanTypeError = new RuntimeException("Loan type service unavailable");

            when(loanApplicationRepository.findApplicationsForReview(pageRequest, null))
                    .thenReturn(Mono.just(pagedApplications));
            when(authServiceGateway.getUsersByIdentityDocuments(anyList()))
                    .thenReturn(Mono.just(users));
            when(loanTypeRepository.findByIds(anyList()))
                    .thenReturn(Mono.error(loanTypeError));

            // When & Then
            StepVerifier.create(loanApplicationUseCase.getApplicationsForReview(pageRequest, null))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                            throwable.getMessage().equals("Loan type service unavailable"))
                    .verify();
        }
    }

    @Nested
    @DisplayName("Edge Cases and Integration Tests")
    class EdgeCasesAndIntegrationTests {

        @Test
        @DisplayName("Should handle null authenticated email gracefully")
        void shouldHandleNullAuthenticatedEmail() {
            // Given
            LoanApplication inputApplication = TestDataBuilder.buildValidLoanApplication();
            LoanType validLoanType = TestDataBuilder.buildValidLoanType();
            User validUser = TestDataBuilder.buildValidUser();

            when(loanTypeRepository.findById(inputApplication.getLoanTypeId()))
                    .thenReturn(Mono.just(validLoanType));
            when(authServiceGateway.getUserByIdentityDocument(inputApplication.getUserIdentityDocument()))
                    .thenReturn(Mono.just(validUser));

            // When & Then
            StepVerifier.create(loanApplicationUseCase.registerLoanApplication(inputApplication, null))
                    .expectError(UnauthorizedOperationException.class)
                    .verify();
        }

        @Test
        @DisplayName("Should handle empty strings in authenticated email")
        void shouldHandleEmptyAuthenticatedEmail() {
            // Given
            LoanApplication inputApplication = TestDataBuilder.buildValidLoanApplication();
            LoanType validLoanType = TestDataBuilder.buildValidLoanType();
            User validUser = TestDataBuilder.buildValidUser();

            when(loanTypeRepository.findById(inputApplication.getLoanTypeId()))
                    .thenReturn(Mono.just(validLoanType));
            when(authServiceGateway.getUserByIdentityDocument(inputApplication.getUserIdentityDocument()))
                    .thenReturn(Mono.just(validUser));

            // When & Then
            StepVerifier.create(loanApplicationUseCase.registerLoanApplication(inputApplication, ""))
                    .expectError(UnauthorizedOperationException.class)
                    .verify();
        }

        @Test
        @DisplayName("Should handle concurrent requests properly")
        void shouldHandleConcurrentRequestsProperly() {
            // Given
            LoanApplication inputApplication = TestDataBuilder.buildValidLoanApplication();
            LoanType validLoanType = TestDataBuilder.buildValidLoanType();
            User validUser = TestDataBuilder.buildValidUser();
            LoanApplication savedApplication = TestDataBuilder.buildSavedLoanApplication();
            String authenticatedEmail = TestDataBuilder.VALID_AUTHENTICATED_EMAIL;

            when(loanTypeRepository.findById(inputApplication.getLoanTypeId()))
                    .thenReturn(Mono.just(validLoanType));
            when(authServiceGateway.getUserByIdentityDocument(inputApplication.getUserIdentityDocument()))
                    .thenReturn(Mono.just(validUser));
            when(loanApplicationRepository.save(any(LoanApplication.class)))
                    .thenReturn(Mono.just(savedApplication));

            // When - Simulate concurrent calls
            Mono<LoanApplication> request1 = loanApplicationUseCase.registerLoanApplication(inputApplication, authenticatedEmail);
            Mono<LoanApplication> request2 = loanApplicationUseCase.registerLoanApplication(inputApplication, authenticatedEmail);

            // Then
            StepVerifier.create(Mono.zip(request1, request2))
                    .expectNextMatches(tuple -> {
                        assert tuple.getT1().equals(savedApplication);
                        assert tuple.getT2().equals(savedApplication);
                        return true;
                    })
                    .verifyComplete();

            // Verify all services were called for both requests
            verify(loanTypeRepository, times(2)).findById(inputApplication.getLoanTypeId());
            verify(authServiceGateway, times(2)).getUserByIdentityDocument(inputApplication.getUserIdentityDocument());
            verify(loanApplicationRepository, times(2)).save(any(LoanApplication.class));
        }
    }
}