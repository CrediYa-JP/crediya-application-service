package co.com.crediya.app.api.mapper;

import co.com.crediya.app.api.dto.request.RegisterLoanApplicationRequest;
import co.com.crediya.app.api.dto.response.LoanApplicationResponse;
import co.com.crediya.app.model.loanapplication.LoanApplication;
import co.com.crediya.app.model.state.enums.LoanApplicationState;
import lombok.NoArgsConstructor;


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

}