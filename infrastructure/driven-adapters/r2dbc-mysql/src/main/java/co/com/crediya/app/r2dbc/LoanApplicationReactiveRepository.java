package co.com.crediya.app.r2dbc;

import co.com.crediya.app.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface LoanApplicationReactiveRepository extends ReactiveCrudRepository<LoanApplicationEntity, Long>,
        ReactiveQueryByExampleExecutor<LoanApplicationEntity> {

    @Query("SELECT * FROM loan_applications WHERE state IN (:states)")
    Flux<LoanApplicationEntity> findByStateIn(List<String> states);
}