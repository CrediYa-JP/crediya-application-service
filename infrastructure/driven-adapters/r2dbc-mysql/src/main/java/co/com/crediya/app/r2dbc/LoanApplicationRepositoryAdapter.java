package co.com.crediya.app.r2dbc;


import co.com.crediya.app.model.loanapplication.LoanApplication;
import co.com.crediya.app.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.app.r2dbc.entity.LoanApplicationEntity;
import co.com.crediya.app.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public class LoanApplicationRepositoryAdapter extends ReactiveAdapterOperations<
           LoanApplication,
          LoanApplicationEntity,
                Long,
                LoanApplicationReactiveRepository
> implements LoanApplicationRepository {

        public LoanApplicationRepositoryAdapter(LoanApplicationReactiveRepository repository, ObjectMapper mapper) {
                super(repository, mapper, entity -> mapper.map(entity, LoanApplication.class));
        }

        @Override
        public Mono<LoanApplication> save(LoanApplication loanApplication) {
                return super.save(loanApplication);
        }

        @Override
        public Mono<LoanApplication> findById(Long applicationId) {
                return super.findById(applicationId);
        }
}

