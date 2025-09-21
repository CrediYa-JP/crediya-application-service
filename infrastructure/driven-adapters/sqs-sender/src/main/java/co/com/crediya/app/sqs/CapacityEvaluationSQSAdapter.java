package co.com.crediya.app.sqs;

import co.com.crediya.app.model.capacityevaluation.CapacityEvaluationMessage;

import co.com.crediya.app.model.capacityevaluation.gateway.CapacityEvaluationGateway;
import co.com.crediya.app.sqs.sender.config.CapacityEvaluationSQSProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
@Slf4j
public class CapacityEvaluationSQSAdapter implements CapacityEvaluationGateway {

    private final SqsAsyncClient sqsAsyncClient;
    private final CapacityEvaluationSQSProperties properties;
    private final ObjectMapper objectMapper;

    public CapacityEvaluationSQSAdapter(@Qualifier("capacityEvaluationSqsClient") SqsAsyncClient sqsAsyncClient,
                                        CapacityEvaluationSQSProperties properties,
                                        ObjectMapper objectMapper) {
        this.sqsAsyncClient = sqsAsyncClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }
    @Override
    public Mono<Void> sendForEvaluation(CapacityEvaluationMessage message) {
        return Mono.fromCallable(() -> convertToJson(message))
                .flatMap(this::sendMessageToSQS)
                .doOnSuccess(messageId -> log.info("CAPACITY_EVALUATION_MESSAGE_SENT applicationId={}, messageId={}",
                        message.getApplicationId(), messageId))
                .doOnError(error -> log.error("CAPACITY_EVALUATION_MESSAGE_FAILED applicationId={}, error={}",
                        message.getApplicationId(), error.getMessage()))
                .then();
    }

    private String convertToJson(CapacityEvaluationMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize capacity evaluation message", e);
        }
    }

    private Mono<String> sendMessageToSQS(String messageBody) {
        return Mono.fromFuture(() ->
                sqsAsyncClient.sendMessage(SendMessageRequest.builder()
                        .queueUrl(properties.queueUrl())
                        .messageBody(messageBody)
                        .build())
        ).map(response -> response.messageId());
    }
}