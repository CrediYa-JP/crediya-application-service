package co.com.crediya.app.model.loantype.gateways;

import co.com.crediya.app.model.loantype.LoanType;
import reactor.core.publisher.Mono;

public interface LoanTypeRepository {
    Mono<LoanType> findById(Long loanTypeId);
}
