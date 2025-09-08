package co.com.crediya.app.r2dbc;


import co.com.crediya.app.model.common.PageRequest;
import co.com.crediya.app.model.common.PagedResult;
import co.com.crediya.app.model.loanapplication.LoanApplication;
import co.com.crediya.app.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.app.r2dbc.entity.LoanApplicationEntity;
import co.com.crediya.app.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;


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
    public Mono<PagedResult<LoanApplication>> findApplicationsForReview(PageRequest domainPageRequest, String statusFilter) {
        List<Long> allowedStateIds = getFilteredStateIds(statusFilter);

        Pageable springPageable = org.springframework.data.domain.PageRequest.of(
                domainPageRequest.getPage(),
                domainPageRequest.getSize()
        );

        return repository.findByStateIdInOrderByCreationDateDesc(allowedStateIds, springPageable)
                .map(this::toEntity)
                .collectList()
                .zipWith(repository.countByStateIdIn(allowedStateIds))
                .map(tuple -> PagedResult.of(
                        tuple.getT1(),
                        domainPageRequest.getPage(),
                        domainPageRequest.getSize(),
                        tuple.getT2()
                ));
    }

    private List<Long> getFilteredStateIds(String statusFilter) {
        Map<String, Long> statusMap = Map.of(
                "pending", 1L,
                "rejected", 3L,
                "manual", 4L
        );

        return Optional.ofNullable(statusFilter)
                .filter(statusMap::containsKey)
                .map(status -> List.of(statusMap.get(status)))
                .orElse(List.of(1L, 3L, 4L));
    }


}

