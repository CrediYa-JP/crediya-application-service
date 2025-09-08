package co.com.crediya.app.usecase;

import co.com.crediya.app.model.common.PagedResult;
import co.com.crediya.app.model.loanapplication.LoanApplication;
import co.com.crediya.app.model.loantype.LoanType;
import co.com.crediya.app.model.user.User;
import co.com.crediya.app.usecase.loanapplication.EnrichedApplicationData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class TestDataBuilder {

    private TestDataBuilder() {
    }

    // ==================== LOAN APPLICATION BUILDERS ====================

    public static LoanApplication buildValidLoanApplication() {
        return LoanApplication.builder()
                .amount(new BigDecimal("10000000"))
                .term(24)
                .loanTypeId(1L)
                .userIdentityDocument("12345678") // CORREGIDO: era userEmail
                .build();
    }

    public static LoanApplication buildSavedLoanApplication() {
        return buildValidLoanApplication().toBuilder()
                .applicationId(1L)
                .stateId(1L) // PENDING_REVIEW
                .creationDate(LocalDateTime.now())
                .lastModificationDate(LocalDateTime.now())
                .build();
    }

    public static LoanApplication buildHighAmountLoanApplication() {
        return buildValidLoanApplication().toBuilder()
                .amount(new BigDecimal("50000000")) // 5x salario base
                .build();
    }

    public static LoanApplication buildWithDifferentUser() {
        return buildValidLoanApplication().toBuilder()
                .userIdentityDocument("87654321")
                .build();
    }

    // ==================== LOAN TYPE BUILDERS ====================

    public static LoanType buildValidLoanType() {
        return LoanType.builder()
                .loanTypeId(1L)
                .name("Personal")
                .minimumAmount(new BigDecimal("1000000"))
                .maximumAmount(new BigDecimal("50000000"))
                .interestRate(new BigDecimal("0.025"))
                .automaticValidation(false)
                .build();
    }

    public static LoanType buildAutomaticValidationLoanType() {
        return buildValidLoanType().toBuilder()
                .loanTypeId(2L)
                .name("Mortgage")
                .automaticValidation(true)
                .build();
    }

    public static List<LoanType> buildLoanTypesList() {
        return List.of(
                buildValidLoanType(),
                buildAutomaticValidationLoanType()
        );
    }

    // ==================== USER BUILDERS ====================

    public static User buildValidUser() {
        return User.builder()
                .email("juan@test.com")
                .firstName("Juan")
                .lastName("Perez")
                .identityDocument("12345678")
                .baseSalary(new BigDecimal("5000000"))
                .build();
    }

    public static User buildDifferentUser() {
        return User.builder()
                .email("maria@test.com")
                .firstName("Maria")
                .lastName("Garcia")
                .identityDocument("87654321")
                .baseSalary(new BigDecimal("6000000"))
                .build();
    }

    public static User buildHighSalaryUser() {
        return buildValidUser().toBuilder()
                .baseSalary(new BigDecimal("15000000"))
                .build();
    }

    public static List<User> buildUsersList() {
        return List.of(
                buildValidUser(),
                buildDifferentUser()
        );
    }

    // ==================== ENRICHED DATA BUILDERS ====================

    public static EnrichedApplicationData buildEnrichedApplicationData() {
        return EnrichedApplicationData.builder()
                .application(buildSavedLoanApplication())
                .user(buildValidUser())
                .loanType(buildValidLoanType())
                .build();
    }

    public static List<EnrichedApplicationData> buildEnrichedApplicationDataList() {
        return List.of(
                buildEnrichedApplicationData(),
                EnrichedApplicationData.builder()
                        .application(buildSavedLoanApplication().toBuilder()
                                .applicationId(2L)
                                .userIdentityDocument("87654321")
                                .build())
                        .user(buildDifferentUser())
                        .loanType(buildValidLoanType())
                        .build()
        );
    }

    // ==================== PAGED RESULT BUILDERS ====================

    public static PagedResult<LoanApplication> buildPagedLoanApplications() {
        List<LoanApplication> applications = List.of(
                buildSavedLoanApplication(),
                buildSavedLoanApplication().toBuilder()
                        .applicationId(2L)
                        .userIdentityDocument("87654321")
                        .build()
        );

        return PagedResult.of(applications, 0, 10, 2L);
    }

    public static PagedResult<LoanApplication> buildEmptyPagedResult() {
        return PagedResult.of(List.of(), 0, 10, 0L);
    }

    // ==================== AUTHENTICATION CONTEXTS ====================

    public static final String VALID_AUTHENTICATED_EMAIL = "juan@test.com";
    public static final String DIFFERENT_AUTHENTICATED_EMAIL = "maria@test.com";
    public static final String UNAUTHORIZED_EMAIL = "hacker@evil.com";

    // ==================== EXCEPTION BUILDERS ====================

    public static RuntimeException buildRepositoryException() {
        return new RuntimeException("Database connection failed");
    }

    public static RuntimeException buildExternalServiceException() {
        return new RuntimeException("Authentication service unavailable");
    }
}