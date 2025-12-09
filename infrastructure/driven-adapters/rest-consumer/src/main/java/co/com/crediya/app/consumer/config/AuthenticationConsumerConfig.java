
package co.com.crediya.app.consumer.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Configuration
public class AuthenticationConsumerConfig {

    private static final String JWT_CONTEXT_KEY = "JWT_TOKEN";  // ← Misma key que el filtro

    private final String url;
    private final int timeout;

    public AuthenticationConsumerConfig(
            @Value("${adapter.auth-service.url}") String url,
            @Value("${adapter.auth-service.timeout}") int timeout) {
        this.url = url;
        this.timeout = timeout;
    }

    @Bean
    public WebClient authServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(url)
                .clientConnector(getClientHttpConnector())
                .filter((request, next) -> {
                    // Leer JWT del Reactor Context (en lugar de SecurityContext)
                    return Mono.deferContextual(contextView -> {
                        if (contextView.hasKey(JWT_CONTEXT_KEY)) {
                            String token = contextView.get(JWT_CONTEXT_KEY);

                            // Agregar Authorization header
                            ClientRequest modifiedRequest = ClientRequest.from(request)
                                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                    .build();

                            return next.exchange(modifiedRequest);
                        }

                        // Si no hay JWT en context, enviar request sin modificar
                        return next.exchange(request);
                    });
                })
                .build();
    }

    private ClientHttpConnector getClientHttpConnector() {
        return new ReactorClientHttpConnector(HttpClient.create()
                .compress(true)
                .keepAlive(true)
                .option(CONNECT_TIMEOUT_MILLIS, timeout)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(timeout, MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(timeout, MILLISECONDS));
                }));
    }
}
