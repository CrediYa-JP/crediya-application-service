package co.com.crediya.app.model.loantype.gateways;

import co.com.crediya.app.model.loantype.LoanType;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LoanTypeRepository {
    Mono<LoanType> findById(Long loanTypeId);
    Mono<List<LoanType>> findByIds(List<Long> loanTypeIds);

}
