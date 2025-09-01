package co.com.crediya.app.model.loanapplication.factory;

import co.com.crediya.app.model.loanapplication.LoanApplication;
import co.com.crediya.app.model.user.User;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@NoArgsConstructor
public final class LoanApplicationFactory {

    public static LoanApplication createPendingApplication(LoanApplication loanApplication, User user) {
        LocalDateTime now = LocalDateTime.now();

        loanApplication.setStateId(1L);
        loanApplication.setUserEmail(user.getEmail());
        loanApplication.setUserName(user.getFullName());
        loanApplication.setUserSalary(user.getSalary());
        loanApplication.setUserDataSnapshotDate(now);
        loanApplication.setCreationDate(now);
        loanApplication.setLastModificationDate(now);
        loanApplication.setStateId(1L); // Estado PENDING_REVIEW

        return loanApplication;
    }
}