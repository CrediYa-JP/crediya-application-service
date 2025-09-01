package co.com.crediya.app.r2dbc;


import co.com.crediya.app.model.loantype.LoanType;
import co.com.crediya.app.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.app.r2dbc.entity.LoanTypeEntity;
import co.com.crediya.app.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

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

}
