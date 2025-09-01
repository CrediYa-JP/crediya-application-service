package co.com.crediya.app.consumer;

import co.com.crediya.app.consumer.dto.UserResponse;
import co.com.crediya.app.consumer.handler.ExternalServiceErrorHandler;
import co.com.crediya.app.consumer.mapper.UserMapConsumer;
import co.com.crediya.app.model.constants.ExternalService;
import co.com.crediya.app.model.user.User;
import co.com.crediya.app.model.user.gateways.AuthServiceGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthenticationConsumer implements AuthServiceGateway {

    private final WebClient client;
    private final ExternalServiceErrorHandler errorHandler;

    private static final String API_V1_USERS = "/api/v1/users";


    @CircuitBreaker(name = "authService")
    @Override
    public Mono<User> getUserByIdentityDocument(String identityDocument) {
        return client
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_V1_USERS + "/retrieve")
                        .queryParam("identityDocument", identityDocument.trim())
                        .build())
                .retrieve()
                .bodyToMono(UserResponse.class)
                .map(UserMapConsumer::mapToUser)
                .onErrorMap(error-> errorHandler.handleServiceError(
                        ExternalService.AUTHENTICATION, error
                ));
    }
}