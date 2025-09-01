package co.com.crediya.app.model.loanapplication.gateways;

import co.com.crediya.app.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;



public interface LoanApplicationRepository {

    Mono<LoanApplication> save(LoanApplication loanApplication);

    Mono<LoanApplication> findById(Long applicationId);

}