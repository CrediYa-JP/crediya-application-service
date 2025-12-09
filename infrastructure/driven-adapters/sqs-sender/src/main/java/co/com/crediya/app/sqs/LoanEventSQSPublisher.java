package co.com.crediya.app.sqs;

import co.com.crediya.app.model.events.LoanApprovalEvent;
import co.com.crediya.app.model.events.gateways.LoanEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanEventSQSPublisher implements LoanEventPublisher {

    private final SqsAsyncClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${adapters.sqs.loan-events.queue-url}")
    private  String queueUrl;

    @Override
    public Mono<Void> publishLoanApprovalEvent(LoanApprovalEvent event) {
        return Mono.fromCallable(() -> serializeEvent(event))
                .flatMap(this::sendToSQS)
                .doOnSuccess(messageId -> log.info("LOAN_EVENT_PUBLISHED applicationId={}, messageId={}",
                        event.getApplicationId(), messageId))
                .doOnError(error -> log.error("LOAN_EVENT_PUBLISH_FAILED applicationId={}, error={}",
                        event.getApplicationId(), error.getMessage()))
                .then();
    }

    private String serializeEvent(LoanApprovalEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize loan approval event", e);
        }
    }

    private Mono<String> sendToSQS(String messageBody) {
        return Mono.fromFuture(() ->
                sqsClient.sendMessage(SendMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .messageBody(messageBody)
                        .build())
        ).map(response -> response.messageId());
    }
}
