package co.com.crediya.app.api;

import co.com.crediya.app.api.dto.request.RegisterLoanApplicationRequest;
import co.com.crediya.app.api.dto.response.EnrichedApplicationResponse;
import co.com.crediya.app.api.dto.response.LoanApplicationResponse;
import co.com.crediya.app.model.loanapplication.LoanApplication;
import co.com.crediya.app.model.loantype.LoanType;
import co.com.crediya.app.model.user.User;
import co.com.crediya.app.usecase.loanapplication.EnrichedApplicationData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public final class RouterTestDataBuilder {

    private RouterTestDataBuilder() {}

    // ================== REQUEST DTOs ==================

    public static RegisterLoanApplicationRequest buildValidRegisterLoanApplicationRequest() {
        return new RegisterLoanApplicationRequest(
                "12345678",
                new BigDecimal("10000000"),
                24,
                1L
        );
    }


    public static RegisterLoanApplicationRequest buildMortgageLoanApplicationRequest() {
        return new RegisterLoanApplicationRequest(
                "12345678",
                new BigDecimal("150000000"),
                240,
                2L
        );
    }
    // ================== ENRICHED APPLICATION DATA (for mocking use cases) ==================

    public static EnrichedApplicationData buildValidEnrichedApplicationData() {
        return EnrichedApplicationData.builder()
                .application(buildValidDomainLoanApplication())
                .user(buildValidDomainUser())
                .loanType(buildValidDomainLoanType())
                .build();
    }

    public static EnrichedApplicationData buildSecondEnrichedApplicationData() {
        return EnrichedApplicationData.builder()
                .application(buildSecondDomainLoanApplication())
                .user(buildSecondDomainUser())
                .loanType(buildValidDomainLoanType())
                .build();
    }

    public static List<EnrichedApplicationData> buildEnrichedApplicationDataList() {
        return Arrays.asList(
                buildValidEnrichedApplicationData(),
                buildSecondEnrichedApplicationData()
        );
    }

    public static RegisterLoanApplicationRequest buildRegisterLoanApplicationRequestWithEdgeCases() {
        return new RegisterLoanApplicationRequest(
                "1234567890",
                new BigDecimal("1000000"),
                6,
                1L
        );
    }

    public static RegisterLoanApplicationRequest buildInvalidRegisterLoanApplicationRequest() {
        return new RegisterLoanApplicationRequest(
                "", // invalid: empty identity document
                BigDecimal.ZERO, // invalid: zero amount
                0, // invalid: zero term
                null // invalid: null loan type
        );
    }

    public static RegisterLoanApplicationRequest buildHighAmountLoanApplicationRequest() {
        return new RegisterLoanApplicationRequest(
                "12345678",
                new BigDecimal("500000000"),
                360,
                3L
        );
    }

    public static RegisterLoanApplicationRequest buildNegativeAmountLoanApplicationRequest() {
        return new RegisterLoanApplicationRequest(
                "12345678",
                new BigDecimal("-1000000"),
                24,
                1L
        );
    }

    // ================== RESPONSE DTOs ==================

    public static LoanApplicationResponse buildValidLoanApplicationResponse() {
        return LoanApplicationResponse.builder()
                .applicationId(1L)
                .amount(new BigDecimal("10000000"))
                .term(24)
                .status("PENDING_REVIEW")
                .creationDate(LocalDateTime.of(2024, 1, 15, 10, 30, 0))
                .build();
    }

    public static LoanApplicationResponse buildSecondLoanApplicationResponse() {
        return LoanApplicationResponse.builder()
                .applicationId(2L)
                .amount(new BigDecimal("5000000"))
                .term(12)
                .status("APPROVED")
                .creationDate(LocalDateTime.of(2024, 1, 16, 14, 45, 0))
                .build();
    }

    public static LoanApplicationResponse buildThirdLoanApplicationResponse() {
        return LoanApplicationResponse.builder()
                .applicationId(3L)
                .amount(new BigDecimal("25000000"))
                .term(48)
                .status("REJECTED")
                .creationDate(LocalDateTime.of(2024, 1, 17, 9, 15, 0))
                .build();
    }

    public static List<LoanApplicationResponse> buildLoanApplicationResponsesList() {
        return Arrays.asList(
                buildValidLoanApplicationResponse(),
                buildSecondLoanApplicationResponse(),
                buildThirdLoanApplicationResponse()
        );
    }

    public static EnrichedApplicationResponse buildValidEnrichedApplicationResponse() {
        return EnrichedApplicationResponse.builder()
                .amount(new BigDecimal("10000000"))
                .term(24)
                .email("juan@test.com")
                .fullName("Juan Perez")
                .loanTypeName("Personal")
                .interestRate(new BigDecimal("0.025"))
                .applicationState("Pending Review")
                .baseSalary(new BigDecimal("5000000"))
                .monthlyPayment(new BigDecimal("461895.41"))
                .creationDate(LocalDateTime.of(2024, 1, 15, 10, 30, 0))
                .build();
    }

    public static EnrichedApplicationResponse buildSecondEnrichedApplicationResponse() {
        return EnrichedApplicationResponse.builder()
                .amount(new BigDecimal("150000000"))
                .term(240)
                .email("maria@test.com")
                .fullName("Maria Gonzalez")
                .loanTypeName("Mortgage")
                .interestRate(new BigDecimal("0.018"))
                .applicationState("Approved")
                .baseSalary(new BigDecimal("8000000"))
                .monthlyPayment(new BigDecimal("3125000.00"))
                .creationDate(LocalDateTime.of(2024, 1, 16, 11, 20, 0))
                .build();
    }

    public static EnrichedApplicationResponse buildThirdEnrichedApplicationResponse() {
        return EnrichedApplicationResponse.builder()
                .amount(new BigDecimal("5000000"))
                .term(12)
                .email("carlos@test.com")
                .fullName("Carlos Lopez")
                .loanTypeName("Personal")
                .interestRate(new BigDecimal("0.025"))
                .applicationState("Rejected")
                .baseSalary(new BigDecimal("3000000"))
                .monthlyPayment(new BigDecimal("230947.70"))
                .creationDate(LocalDateTime.of(2024, 1, 17, 9, 15, 0))
                .build();
    }

    public static List<EnrichedApplicationResponse> buildEnrichedApplicationResponsesList() {
        return Arrays.asList(
                buildValidEnrichedApplicationResponse(),
                buildSecondEnrichedApplicationResponse(),
                buildThirdEnrichedApplicationResponse()
        );
    }

    // ================== DOMAIN MODELS (for mocking use cases) ==================

    public static LoanApplication buildValidDomainLoanApplication() {
        return LoanApplication.builder()
                .applicationId(1L)
                .amount(new BigDecimal("10000000"))
                .term(24)
                .stateId(1L) // PENDING_REVIEW
                .loanTypeId(1L) // PERSONAL
                .userIdentityDocument("12345678")
                .creationDate(LocalDateTime.of(2024, 1, 15, 10, 30, 0))
                .lastModificationDate(LocalDateTime.of(2024, 1, 15, 10, 30, 0))
                .build();
    }

    public static LoanApplication buildSecondDomainLoanApplication() {
        return LoanApplication.builder()
                .applicationId(2L)
                .amount(new BigDecimal("5000000"))
                .term(12)
                .stateId(2L) // APPROVED
                .loanTypeId(1L) // PERSONAL
                .userIdentityDocument("87654321")
                .creationDate(LocalDateTime.of(2024, 1, 16, 14, 45, 0))
                .lastModificationDate(LocalDateTime.of(2024, 1, 16, 15, 0, 0))
                .build();
    }

    public static LoanApplication buildThirdDomainLoanApplication() {
        return LoanApplication.builder()
                .applicationId(3L)
                .amount(new BigDecimal("25000000"))
                .term(48)
                .stateId(3L) // REJECTED
                .loanTypeId(1L) // PERSONAL
                .userIdentityDocument("11223344")
                .creationDate(LocalDateTime.of(2024, 1, 17, 9, 15, 0))
                .lastModificationDate(LocalDateTime.of(2024, 1, 17, 10, 0, 0))
                .build();
    }

    public static LoanApplication buildMortgageDomainLoanApplication() {
        return LoanApplication.builder()
                .applicationId(4L)
                .amount(new BigDecimal("150000000"))
                .term(240)
                .stateId(1L) // PENDING_REVIEW
                .loanTypeId(2L) // MORTGAGE
                .userIdentityDocument("12345678")
                .creationDate(LocalDateTime.of(2024, 1, 16, 11, 20, 0))
                .lastModificationDate(LocalDateTime.of(2024, 1, 16, 11, 20, 0))
                .build();
    }

    public static LoanApplication buildApprovedDomainLoanApplication() {
        return LoanApplication.builder()
                .applicationId(5L)
                .amount(new BigDecimal("5000000"))
                .term(12)
                .stateId(2L) // APPROVED
                .loanTypeId(1L) // PERSONAL
                .userIdentityDocument("12345678")
                .creationDate(LocalDateTime.of(2024, 1, 17, 9, 15, 0))
                .lastModificationDate(LocalDateTime.of(2024, 1, 17, 14, 30, 0))
                .build();
    }

    public static LoanApplication buildDomainLoanApplicationWithNullableFields() {
        return LoanApplication.builder()
                .applicationId(6L)
                .amount(new BigDecimal("2000000"))
                .term(18)
                .stateId(1L) // PENDING_REVIEW
                .loanTypeId(1L) // PERSONAL
                .userIdentityDocument("99887766")
                .creationDate(LocalDateTime.of(2024, 1, 18, 8, 0, 0))
                .lastModificationDate(LocalDateTime.of(2024, 1, 18, 8, 0, 0))
                .build();
    }

    public static List<LoanApplication> buildDomainLoanApplicationsList() {
        return Arrays.asList(
                buildValidDomainLoanApplication(),
                buildSecondDomainLoanApplication(),
                buildThirdDomainLoanApplication()
        );
    }

    // ================== DOMAIN SUPPORTING MODELS ==================

    public static User buildValidDomainUser() {
        return User.builder()
                .email("juan@test.com")
                .firstName("Juan")
                .lastName("Perez")
                .identityDocument("12345678")
                .baseSalary(new BigDecimal("5000000"))
                .build();
    }

    public static User buildSecondDomainUser() {
        return User.builder()
                .email("maria@test.com")
                .firstName("Maria")
                .lastName("Gonzalez")
                .identityDocument("87654321")
                .baseSalary(new BigDecimal("8000000"))
                .build();
    }

    public static User buildThirdDomainUser() {
        return User.builder()
                .email("carlos@test.com")
                .firstName("Carlos")
                .lastName("Lopez")
                .identityDocument("11223344")
                .baseSalary(new BigDecimal("3000000"))
                .build();
    }

    public static User buildDomainUserWithNullableFields() {
        return User.builder()
                .email("ana@test.com")
                .firstName("Ana")
                .lastName("Silva")
                .identityDocument("99887766")
                .baseSalary(new BigDecimal("2500000"))
                .build();
    }

    public static List<User> buildDomainUsersList() {
        return Arrays.asList(
                buildValidDomainUser(),
                buildSecondDomainUser(),
                buildThirdDomainUser()
        );
    }

    public static LoanType buildValidDomainLoanType() {
        return LoanType.builder()
                .loanTypeId(1L)
                .name("Personal")
                .minimumAmount(new BigDecimal("1000000"))
                .maximumAmount(new BigDecimal("50000000"))
                .interestRate(new BigDecimal("0.025"))
                .automaticValidation(false)
                .build();
    }

    public static LoanType buildSecondDomainLoanType() {
        return LoanType.builder()
                .loanTypeId(2L)
                .name("Mortgage")
                .minimumAmount(new BigDecimal("20000000"))
                .maximumAmount(new BigDecimal("500000000"))
                .interestRate(new BigDecimal("0.018"))
                .automaticValidation(true)
                .build();
    }

    public static LoanType buildThirdDomainLoanType() {
        return LoanType.builder()
                .loanTypeId(3L)
                .name("Vehicle")
                .minimumAmount(new BigDecimal("5000000"))
                .maximumAmount(new BigDecimal("100000000"))
                .interestRate(new BigDecimal("0.022"))
                .automaticValidation(false)
                .build();
    }

    public static LoanType buildMortgageDomainLoanType() {
        return buildSecondDomainLoanType();
    }

    public static LoanType buildVehicleDomainLoanType() {
        return buildThirdDomainLoanType();
    }

    public static List<LoanType> buildDomainLoanTypesList() {
        return Arrays.asList(
                buildValidDomainLoanType(),
                buildSecondDomainLoanType(),
                buildThirdDomainLoanType()
        );
    }

    // ================== UTILITY ARRAYS FOR BATCH OPERATIONS ==================

    public static String[] buildValidIdentityDocumentsArray() {
        return new String[]{"12345678", "87654321", "11223344"};
    }

    public static String[] buildEmptyIdentityDocumentsArray() {
        return new String[]{};
    }

    public static String[] buildSingleIdentityDocumentArray() {
        return new String[]{"12345678"};
    }

    public static Long[] buildValidLoanTypeIdsArray() {
        return new Long[]{1L, 2L, 3L};
    }

    public static Long[] buildInvalidLoanTypeIdsArray() {
        return new Long[]{999L, 1000L};
    }
}