package co.com.crediya.app.r2dbc;

import co.com.crediya.app.model.loanapplication.LoanApplication;
import co.com.crediya.app.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LoanApplicationReactiveRepository extends ReactiveCrudRepository<LoanApplicationEntity, Long>,
        ReactiveQueryByExampleExecutor<LoanApplicationEntity> {
    Flux<LoanApplicationEntity> findByStateIdInOrderByCreationDateDesc(List<Long> stateIds, Pageable pageable);

    Mono<Long> countByStateIdIn(List<Long> stateIds);
}