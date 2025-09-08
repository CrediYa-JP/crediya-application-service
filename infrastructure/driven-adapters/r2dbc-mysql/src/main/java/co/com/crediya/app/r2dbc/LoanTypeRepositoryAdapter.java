package co.com.crediya.app.r2dbc;


import co.com.crediya.app.model.loanapplication.LoanApplication;
import co.com.crediya.app.model.loantype.LoanType;
import co.com.crediya.app.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.app.r2dbc.entity.LoanTypeEntity;
import co.com.crediya.app.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class LoanTypeRepositoryAdapter extends ReactiveAdapterOperations<
        LoanType,
        LoanTypeEntity,
        Long,
        LoanTypeReactiveRepository> implements LoanTypeRepository {

    public LoanTypeRepositoryAdapter(LoanTypeReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, LoanType.class));
    }

    @Override
    public Mono<LoanType> findById(Long loanTypeId){
        return super.findById(loanTypeId);
    }
    @Override
    public Mono<List<LoanType>> findByIds(List<Long> loanTypeIds) {
        return repository.findAllById(loanTypeIds)
                .map(this::toEntity)
                .collectList();
    }

}
