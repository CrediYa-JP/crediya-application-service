package co.com.crediya.app.usecase.loanapplication;
import co.com.crediya.app.model.loanapplication.LoanApplication;
import co.com.crediya.app.model.loantype.LoanType;
import co.com.crediya.app.model.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EnrichedApplicationData {
    private final LoanApplication application;
    private final User user;
    private final LoanType loanType;
}