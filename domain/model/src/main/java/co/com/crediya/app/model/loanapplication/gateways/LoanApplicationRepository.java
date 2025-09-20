package co.com.crediya.app.model.loanapplication.gateways;

import co.com.crediya.app.model.common.PageRequest;
import co.com.crediya.app.model.common.PagedResult;
import co.com.crediya.app.model.loanapplication.LoanApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.print.Pageable;
import java.util.List;


public interface LoanApplicationRepository {

    Mono<LoanApplication>findById(Long id);
    Mono<LoanApplication> save(LoanApplication loanApplication);

    Mono<PagedResult<LoanApplication>> findApplicationsForReview(PageRequest pageRequest, String statusFilter);

    Flux<LoanApplication> findApprovedApplicationsByUser(String userIdentityDocument);


}