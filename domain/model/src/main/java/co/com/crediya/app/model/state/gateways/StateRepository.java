package co.com.crediya.app.model.state.gateways;

import co.com.crediya.app.model.state.State;
import reactor.core.publisher.Mono;

public interface StateRepository {
    Mono<State> findById(Long stateId);
    Mono<State>findByName(String name);


}
