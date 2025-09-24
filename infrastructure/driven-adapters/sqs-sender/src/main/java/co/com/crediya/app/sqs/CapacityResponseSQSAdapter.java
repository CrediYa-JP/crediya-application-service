package co.com.crediya.app.sqs;

import co.com.crediya.app.model.capacityevaluation.CapacityEvaluationResponse;

import co.com.crediya.app.model.capacityevaluation.gateway.CapacityResponseGateway;
import co.com.crediya.app.sqs.sender.config.CapacityResponseSQSProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Component
@Slf4j
public class CapacityResponseSQSAdapter implements CapacityResponseGateway {

    private final SqsAsyncClient sqsAsyncClient;
    private final CapacityResponseSQSProperties properties;
    private final ObjectMapper objectMapper;
    public CapacityResponseSQSAdapter(@Qualifier("capacityResponseSqsClient") SqsAsyncClient sqsAsyncClient,
                                      CapacityResponseSQSProperties properties,
                                      ObjectMapper objectMapper) {
        this.sqsAsyncClient = sqsAsyncClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public Flux<CapacityEvaluationResponse> pollForResponses() {
        return Mono.fromFuture(() ->
                        sqsAsyncClient.receiveMessage(ReceiveMessageRequest.builder()
                                .queueUrl(properties.queueUrl())
                                .maxNumberOfMessages(10)
                                .waitTimeSeconds(1)
                                .build())
                )
                .flatMapMany(response -> Flux.fromIterable(response.messages()))
                .flatMap(this::parseMessage)
                .doOnNext(response -> log.info("CAPACITY_RESPONSE_RECEIVED applicationId={}, decision={}",
                        response.getApplicationId(), response.getDecision()));
    }

    @Override
    public Mono<Void> deleteMessage(String receiptHandle) {
        return Mono.fromFuture(() ->
                sqsAsyncClient.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(properties.queueUrl())
                        .receiptHandle(receiptHandle)
                        .build())
        ).then();
    }

    private Mono<CapacityEvaluationResponse> parseMessage(Message message) {
        try {
            CapacityEvaluationResponse response = objectMapper.readValue(
                    message.body(), CapacityEvaluationResponse.class);

            // Eliminar mensaje después de procesar exitosamente
            return deleteMessage(message.receiptHandle())
                    .thenReturn(response);

        } catch (Exception e) {
            log.error("ERROR parsing capacity response message: {}", e.getMessage());
            return Mono.empty();
        }
    }
}