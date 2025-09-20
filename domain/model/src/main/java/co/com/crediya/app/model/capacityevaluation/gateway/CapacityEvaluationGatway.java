package co.com.crediya.app.model.capacityevaluation.gateway;

import co.com.crediya.app.model.capacityevaluation.CapacityEvaluationMessage;
import reactor.core.publisher.Mono;

public interface CapacityEvaluationGatway {
    Mono<Void> sendForEvaluation(CapacityEvaluationMessage message);
}