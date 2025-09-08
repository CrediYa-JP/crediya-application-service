package co.com.crediya.app.api.mapper;

import co.com.crediya.app.api.dto.request.RegisterLoanApplicationRequest;
import co.com.crediya.app.api.dto.response.EnrichedApplicationResponse;
import co.com.crediya.app.api.dto.response.LoanApplicationResponse;
import co.com.crediya.app.model.loanapplication.LoanApplication;
import co.com.crediya.app.model.loanapplication.constants.ApplicationState;
import co.com.crediya.app.model.loantype.LoanType;
import co.com.crediya.app.model.state.enums.LoanApplicationState;
import co.com.crediya.app.model.user.User;
import co.com.crediya.app.model.utils.LoanCalculationUtil;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@NoArgsConstructor
public final class LoanApplicationMapper {

    public static LoanApplication toDomain(RegisterLoanApplicationRequest request) {
        return LoanApplication.builder()
                .amount(request.getAmount())
                .term(request.getTerm())
                .loanTypeId(request.getLoanTypeId())
                .userIdentityDocument(request.getIdentityDocument())
                .build();
    }

    public static LoanApplicationResponse toResponse(LoanApplication loanApplication) {
        return LoanApplicationResponse.builder()
                .applicationId(loanApplication.getApplicationId())
                .amount(loanApplication.getAmount())
                .term(loanApplication.getTerm())
                .status(LoanApplicationState.fromId(loanApplication.getStateId()).name())
                .creationDate(loanApplication.getCreationDate())
                .build();
    }

    public static EnrichedApplicationResponse toEnrichedResponse(LoanApplication application,
                                                                 User user,
                                                                 LoanType loanType) {

        BigDecimal monthlyPayment = LoanCalculationUtil.calculateMonthlyPayment(
                application.getAmount(),
                loanType.getInterestRate(),
                application.getTerm());

        return EnrichedApplicationResponse.builder()
                .amount(application.getAmount())
                .term(application.getTerm())
                .email(user.getEmail())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .loanTypeName(loanType.getName())
                .interestRate(loanType.getInterestRate())
                .applicationState(ApplicationState.getNameById(application.getStateId()))
                .baseSalary(user.getBaseSalary())
                .monthlyPayment(monthlyPayment)
                .creationDate(application.getCreationDate())
                .build();
    }

}