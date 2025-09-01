package co.com.crediya.app.usecase.util;

import co.com.crediya.app.model.loanapplication.LoanApplication;
import co.com.crediya.app.model.loantype.LoanType;
import co.com.crediya.app.model.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class TestDataBuilder {

    private TestDataBuilder() {
    }

    public static LoanApplication buildValidLoanApplication() {
        return LoanApplication.builder()
                .amount(new BigDecimal("10000000"))
                .term(24)
                .loanTypeId(1L)
                .userEmail("juan@test.com")
                .build();
    }

    public static LoanApplication buildSavedLoanApplication() {
        return buildValidLoanApplication().toBuilder()
                .applicationId(1L)
                .stateId(1L)
                .userName("Juan Perez")
                .userSalary(new BigDecimal("5000000"))
                .userDataSnapshotDate(LocalDateTime.now())
                .creationDate(LocalDateTime.now())
                .lastModificationDate(LocalDateTime.now())
                .build();
    }

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

    public static User buildValidUser() {
        return User.builder()
                .email("juan@test.com")
                .firstName("Juan")
                .lastName("Perez")
                .salary(new BigDecimal("5000000"))
                .build();
    }
}