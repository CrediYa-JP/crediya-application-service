package co.com.crediya.app.model.capacityevaluation.gateway;

import co.com.crediya.app.model.capacityevaluation.CapacityEvaluationResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacityResponseGateway {
    Flux<CapacityEvaluationResponse> pollForResponses();
    Mono<Void> deleteMessage(String receiptHandle);
}