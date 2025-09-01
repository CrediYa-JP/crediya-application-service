package co.com.crediya.app.r2dbc;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface LoanApplicationTypeReactiveRepository extends ReactiveCrudRepository<LoanApplicationTypeEntity, Long>,
        ReactiveQueryByExampleExecutor<LoanApplicationEntity>{
}
