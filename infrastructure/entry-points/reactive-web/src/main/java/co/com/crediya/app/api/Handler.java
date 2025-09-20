package co.com.crediya.app.api;

import co.com.crediya.app.api.dto.request.RegisterLoanApplicationRequest;
import co.com.crediya.app.api.dto.request.UpdateApplicationStatusRequest;
import co.com.crediya.app.api.dto.response.EnrichedApplicationResponse;
import co.com.crediya.app.api.dto.response.PagedResultResponse;
import co.com.crediya.app.api.mapper.LoanApplicationMapper;
import co.com.crediya.app.api.util.ValidationUtil;
import co.com.crediya.app.model.common.PageRequest;
import co.com.crediya.app.usecase.loanapplication.LoanApplicationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private final LoanApplicationUseCase loanApplicationUseCase;

    public Mono<ServerResponse> getApplicationsForReview(ServerRequest serverRequest) {
        int page = serverRequest.queryParam("page").map(Integer::parseInt).orElse(0);
        int size = serverRequest.queryParam("size").map(Integer::parseInt).orElse(10);
        String statusFilter = serverRequest.queryParam("status").orElse(null);

        PageRequest pageRequest = PageRequest.of(page, size);

        return loanApplicationUseCase.getApplicationsForReview(pageRequest, statusFilter)
                .map(pagedResult -> {
                    List<EnrichedApplicationResponse> responses = pagedResult.getContent().stream()
                            .map(enrichedData -> LoanApplicationMapper.toEnrichedResponse(
                                    enrichedData.getApplication(),
                                    enrichedData.getUser(),
                                    enrichedData.getLoanType()))
                            .toList();

                    return PagedResultResponse.<EnrichedApplicationResponse>builder()
                            .content(responses)
                            .pageNumber(pagedResult.getPageNumber())
                            .pageSize(pagedResult.getPageSize())
                            .totalElements(pagedResult.getTotalElements())
                            .totalPages(pagedResult.getTotalPages())
                            .hasNext(pagedResult.isHasNext())
                            .hasPrevious(pagedResult.isHasPrevious())
                            .build();
                })
                .flatMap(result -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(result));
    }

    public Mono<ServerResponse> registerLoanApplication(ServerRequest serverRequest) {
        String authenticatedEmail = (String) serverRequest.exchange()
                .getAttributes().get("authenticatedEmail");

        return serverRequest.bodyToMono(RegisterLoanApplicationRequest.class)
                .flatMap(ValidationUtil::validate)
                .doOnNext(req -> log.info("LOAN_APPLICATION_REQUEST authenticatedEmail={}, requestEmail={}",
                        authenticatedEmail, req.getIdentityDocument()))
                .map(LoanApplicationMapper::toDomain)
                .flatMap(loanApplication -> loanApplicationUseCase.registerLoanApplication(
                        loanApplication, authenticatedEmail))
                .map(LoanApplicationMapper::toResponse)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }
    public Mono<ServerResponse> updateApplicationStatus(ServerRequest serverRequest) {
        String applicationId = serverRequest.pathVariable("id");

        return serverRequest.bodyToMono(UpdateApplicationStatusRequest.class)
                .flatMap(ValidationUtil::validate)
                .doOnNext(req -> log.info("UPDATE_APPLICATION_STATUS applicationId={}, status={}",
                        applicationId, req.getStatus()))
                .flatMap(request -> loanApplicationUseCase.updateApplicationStatus(
                        Long.valueOf(applicationId),
                        request.getLoanApplicationState()))
                .map(LoanApplicationMapper::toUpdateStatusResponse)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }
}