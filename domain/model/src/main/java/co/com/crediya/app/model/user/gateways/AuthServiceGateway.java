package co.com.crediya.app.model.user.gateways;

import co.com.crediya.app.model.user.User;
import reactor.core.publisher.Mono;

public interface AuthServiceGateway {
    Mono<User> getUserByIdentityDocument(String email);

}
