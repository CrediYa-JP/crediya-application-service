package co.com.crediya.app.model.user.gateways;

import co.com.crediya.app.model.user.User;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AuthServiceGateway {
    Mono<User> getUserByIdentityDocument(String email);
    Mono<List<User>> getUsersByIdentityDocuments(List<String> identityDocuments);


}
