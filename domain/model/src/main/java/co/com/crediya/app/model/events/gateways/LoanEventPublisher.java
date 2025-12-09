package co.com.crediya.app.model.events.gateways;

import co.com.crediya.app.model.events.LoanApprovalEvent;
import reactor.core.publisher.Mono;

public interface LoanEventPublisher {
    Mono<Void> publishLoanApprovalEvent(LoanApprovalEvent event);
}