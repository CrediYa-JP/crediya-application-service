package co.com.crediya.app.api;

import co.com.crediya.app.api.dto.request.RegisterLoanApplicationRequest;
import co.com.crediya.app.api.mapper.LoanApplicationMapper;
import co.com.crediya.app.api.util.ValidationUtil;
import co.com.crediya.app.usecase.loanapplication.LoanApplicationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private final LoanApplicationUseCase loanApplicationUseCase;

    public Mono<ServerResponse> registerLoanApplication(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(RegisterLoanApplicationRequest.class)
                .flatMap(ValidationUtil::validate)
                .doOnNext(req -> log.info("LOAN_APPLICATION_REGISTER_REQUEST userIdentityDocument={} loanTypeId={}",
                        req.getIdentityDocument(), req.getLoanTypeId()))
                .map(LoanApplicationMapper::toDomain)
                .flatMap(loanApplicationUseCase::registerLoanApplication)
                .map(LoanApplicationMapper::toResponse)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }
}