package co.com.crediya.app.api;

import co.com.crediya.app.api.dto.request.RegisterLoanApplicationRequest;
import co.com.crediya.app.api.dto.response.LoanApplicationResponse;
import co.com.crediya.app.api.dto.response.PagedResultResponse;
import co.com.crediya.app.api.exception.GlobalExceptionHandler;
import co.com.crediya.app.model.common.PageRequest;
import co.com.crediya.app.model.common.PagedResult;
import co.com.crediya.app.model.exception.loanapplication.InvalidLoanTypeException;
import co.com.crediya.app.model.exception.common.UnauthorizedOperationException;
import co.com.crediya.app.model.exception.loanapplication.UserNotFoundException;
import co.com.crediya.app.model.loanapplication.LoanApplication;
import co.com.crediya.app.usecase.loanapplication.EnrichedApplicationData;
import co.com.crediya.app.usecase.loanapplication.LoanApplicationUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {
        RouterRest.class,
        Handler.class,
        GlobalExceptionHandler.class,
        RouterRestTest.TestAuthFilter.class
})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private LoanApplicationUseCase loanApplicationUseCase;

    // ================== TEST AUTH FILTER ==================
    @Component
    static class TestAuthFilter implements WebFilter {
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
            exchange.getAttributes().put("authenticatedEmail", "juan@test.com");
            return chain.filter(exchange);
        }
    }

    // ================== POST /api/v1/applications - REGISTER LOAN APPLICATION TESTS ==================

    @Test
    @DisplayName("Should register loan application successfully")
    void shouldRegisterLoanApplicationSuccessfully() {
        // Arrange
        RegisterLoanApplicationRequest request = RouterTestDataBuilder.buildValidRegisterLoanApplicationRequest();
        LoanApplication mockLoanApplication = RouterTestDataBuilder.buildValidDomainLoanApplication();

        when(loanApplicationUseCase.registerLoanApplication(any(LoanApplication.class), eq("juan@test.com")))
                .thenReturn(Mono.just(mockLoanApplication));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(LoanApplicationResponse.class)
                .value(response -> {
                    assertThat(response.getApplicationId()).isEqualTo(1L);
                    assertThat(response.getAmount()).isEqualTo("10000000");
                    assertThat(response.getTerm()).isEqualTo(24);
                    assertThat(response.getStatus()).isEqualTo("PENDING_REVIEW");
                });
    }

    @Test
    @DisplayName("Should handle validation errors in register loan application request")
    void shouldHandleValidationErrorsInRegisterLoanApplication() {
        // Arrange
        RegisterLoanApplicationRequest invalidRequest = RouterTestDataBuilder.buildInvalidRegisterLoanApplicationRequest();

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @DisplayName("Should handle user not found exception")
    void shouldHandleUserNotFoundException() {
        // Arrange
        RegisterLoanApplicationRequest request = RouterTestDataBuilder.buildValidRegisterLoanApplicationRequest();

        when(loanApplicationUseCase.registerLoanApplication(any(LoanApplication.class), eq("juan@test.com")))
                .thenReturn(Mono.error(new UserNotFoundException("juan@test.com")));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @DisplayName("Should handle invalid loan type exception")
    void shouldHandleInvalidLoanTypeException() {
        // Arrange
        RegisterLoanApplicationRequest request = RouterTestDataBuilder.buildValidRegisterLoanApplicationRequest();

        when(loanApplicationUseCase.registerLoanApplication(any(LoanApplication.class), eq("juan@test.com")))
                .thenReturn(Mono.error(new InvalidLoanTypeException()));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @DisplayName("Should handle unauthorized operation exception")
    void shouldHandleUnauthorizedOperationException() {
        // Arrange
        RegisterLoanApplicationRequest request = RouterTestDataBuilder.buildValidRegisterLoanApplicationRequest();

        when(loanApplicationUseCase.registerLoanApplication(any(LoanApplication.class), eq("juan@test.com")))
                .thenReturn(Mono.error(new UnauthorizedOperationException()));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @DisplayName("Should handle mortgage loan application successfully")
    void shouldHandleMortgageLoanApplicationSuccessfully() {
        // Arrange
        RegisterLoanApplicationRequest request = RouterTestDataBuilder.buildMortgageLoanApplicationRequest();
        LoanApplication mockLoanApplication = RouterTestDataBuilder.buildMortgageDomainLoanApplication();

        when(loanApplicationUseCase.registerLoanApplication(any(LoanApplication.class), eq("juan@test.com")))
                .thenReturn(Mono.just(mockLoanApplication));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(LoanApplicationResponse.class)
                .value(response -> {
                    assertThat(response.getApplicationId()).isEqualTo(4L);
                    assertThat(response.getAmount()).isEqualTo("150000000");
                    assertThat(response.getTerm()).isEqualTo(240);
                    assertThat(response.getStatus()).isEqualTo("PENDING_REVIEW");
                });
    }

    @Test
    @DisplayName("Should handle negative amount in loan application request")
    void shouldHandleNegativeAmountInLoanApplication() {
        // Arrange
        RegisterLoanApplicationRequest invalidRequest = RouterTestDataBuilder.buildNegativeAmountLoanApplicationRequest();

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @DisplayName("Should handle malformed JSON in register loan application request")
    void shouldHandleMalformedJsonInRegisterLoanApplication() {
        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("{ invalid json")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Should handle loan application service error")
    void shouldHandleLoanApplicationServiceError() {
        // Arrange
        RegisterLoanApplicationRequest request = RouterTestDataBuilder.buildValidRegisterLoanApplicationRequest();

        when(loanApplicationUseCase.registerLoanApplication(any(LoanApplication.class), eq("juan@test.com")))
                .thenReturn(Mono.error(new RuntimeException("Loan application service unavailable")));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    // ================== GET /api/v1/applications - GET APPLICATIONS FOR REVIEW TESTS ==================

    @Test
    @DisplayName("Should get applications for review successfully")
    void shouldGetApplicationsForReviewSuccessfully() {
        // Arrange
        List<EnrichedApplicationData> mockApplicationsData = RouterTestDataBuilder.buildEnrichedApplicationDataList();
        PagedResult<EnrichedApplicationData> pagedResult = PagedResult.of(mockApplicationsData, 0, 10, 2L);

        when(loanApplicationUseCase.getApplicationsForReview(any(PageRequest.class), eq(null)))
                .thenReturn(Mono.just(pagedResult));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/applications?page=0&size=10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(PagedResultResponse.class)
                .value(result -> {
                    assertThat(result.getContent()).hasSize(2);
                    assertThat(result.getTotalElements()).isEqualTo(2L);
                    assertThat(result.getTotalPages()).isEqualTo(1);
                });
    }

    @Test
    @DisplayName("Should get applications for review with status filter successfully")
    void shouldGetApplicationsForReviewWithStatusFilterSuccessfully() {
        // Arrange
        List<EnrichedApplicationData> mockApplicationsData = List.of(RouterTestDataBuilder.buildValidEnrichedApplicationData());
        PagedResult<EnrichedApplicationData> pagedResult = PagedResult.of(mockApplicationsData, 0, 10, 1L);

        when(loanApplicationUseCase.getApplicationsForReview(any(PageRequest.class), eq("pending")))
                .thenReturn(Mono.just(pagedResult));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/applications?page=0&size=10&status=pending")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(PagedResultResponse.class)
                .value(result -> {
                    assertThat(result.getContent()).hasSize(1);
                    assertThat(result.getTotalElements()).isEqualTo(1L);
                });
    }

    @Test
    @DisplayName("Should handle empty results for applications review")
    void shouldHandleEmptyResultsForApplicationsReview() {
        // Arrange
        PagedResult<EnrichedApplicationData> emptyPagedResult = PagedResult.of(List.of(), 0, 10, 0L);

        when(loanApplicationUseCase.getApplicationsForReview(any(PageRequest.class), eq(null)))
                .thenReturn(Mono.just(emptyPagedResult));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/applications?page=0&size=10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(PagedResultResponse.class)
                .value(result -> {
                    assertThat(result.getContent()).isEmpty();
                    assertThat(result.getTotalElements()).isEqualTo(0L);
                    assertThat(result.getTotalPages()).isEqualTo(0);
                });
    }

    @Test
    @DisplayName("Should handle unauthorized access to applications review")
    void shouldHandleUnauthorizedAccessToApplicationsReview() {
        // Arrange
        when(loanApplicationUseCase.getApplicationsForReview(any(PageRequest.class), eq(null)))
                .thenReturn(Mono.error(new UnauthorizedOperationException()));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/applications?page=0&size=10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @DisplayName("Should handle applications review service error")
    void shouldHandleApplicationsReviewServiceError() {
        // Arrange
        when(loanApplicationUseCase.getApplicationsForReview(any(PageRequest.class), eq(null)))
                .thenReturn(Mono.error(new RuntimeException("Database connection failed")));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/applications?page=0&size=10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @DisplayName("Should handle pagination with large page numbers")
    void shouldHandlePaginationWithLargePageNumbers() {
        // Arrange
        PagedResult<EnrichedApplicationData> emptyPagedResult = PagedResult.of(List.of(), 100, 10, 5L);

        when(loanApplicationUseCase.getApplicationsForReview(any(PageRequest.class), eq(null)))
                .thenReturn(Mono.just(emptyPagedResult));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/applications?page=100&size=10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(PagedResultResponse.class)
                .value(result -> {
                    assertThat(result.getContent()).isEmpty();
                    assertThat(result.getPageNumber()).isEqualTo(100);
                    assertThat(result.getPageSize()).isEqualTo(10);
                });
    }

    // ================== EDGE CASES AND INTEGRATION TESTS ==================

    @Test
    @DisplayName("Should handle missing Accept header")
    void shouldHandleMissingAcceptHeader() {
        // Arrange
        RegisterLoanApplicationRequest request = RouterTestDataBuilder.buildValidRegisterLoanApplicationRequest();
        LoanApplication mockLoanApplication = RouterTestDataBuilder.buildValidDomainLoanApplication();

        when(loanApplicationUseCase.registerLoanApplication(any(LoanApplication.class), eq("juan@test.com")))
                .thenReturn(Mono.just(mockLoanApplication));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should handle high amount loan application")
    void shouldHandleHighAmountLoanApplication() {
        // Arrange
        RegisterLoanApplicationRequest request = RouterTestDataBuilder.buildHighAmountLoanApplicationRequest();
        LoanApplication mockLoanApplication = RouterTestDataBuilder.buildValidDomainLoanApplication();

        when(loanApplicationUseCase.registerLoanApplication(any(LoanApplication.class), eq("juan@test.com")))
                .thenReturn(Mono.just(mockLoanApplication));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(LoanApplicationResponse.class)
                .value(response -> {
                    assertThat(response.getApplicationId()).isEqualTo(1L);
                    assertThat(response.getStatus()).isEqualTo("PENDING_REVIEW");
                });
    }

    @Test
    @DisplayName("Should handle custom page size in applications review")
    void shouldHandleCustomPageSizeInApplicationsReview() {
        // Arrange
        List<EnrichedApplicationData> mockApplicationsData = RouterTestDataBuilder.buildEnrichedApplicationDataList();
        PagedResult<EnrichedApplicationData> pagedResult = PagedResult.of(mockApplicationsData, 0, 5, 2L);

        when(loanApplicationUseCase.getApplicationsForReview(any(PageRequest.class), eq(null)))
                .thenReturn(Mono.just(pagedResult));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/applications?page=0&size=5")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.pageSize").isEqualTo(5)
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(2);
    }
}