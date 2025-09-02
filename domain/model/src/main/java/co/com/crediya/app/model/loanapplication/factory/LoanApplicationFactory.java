package co.com.crediya.app.model.loanapplication.factory;

import co.com.crediya.app.model.loanapplication.LoanApplication;
import co.com.crediya.app.model.user.User;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@NoArgsConstructor
public final class LoanApplicationFactory {

    public static LoanApplication createPendingApplication(LoanApplication loanApplication) {
        LocalDateTime now = LocalDateTime.now();
        loanApplication.setLoanTypeId(loanApplication.getLoanTypeId());
        loanApplication.setUserIdentityDocument(loanApplication.getUserIdentityDocument());
        loanApplication.setCreationDate(now);
        loanApplication.setLastModificationDate(now);
        loanApplication.setStateId(1L);

        return loanApplication;
    }
}