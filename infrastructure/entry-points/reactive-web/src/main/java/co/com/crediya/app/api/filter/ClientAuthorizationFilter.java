package co.com.crediya.app.api.filter;

import co.com.crediya.app.security.JwtValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class ClientAuthorizationFilter implements WebFilter {

    private static final String CLIENT_ROLE = "ROLE_CUSTOMER";
    private final JwtValidationUtil jwtValidationUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        HttpMethod method = exchange.getRequest().getMethod();

        if (shouldProtectEndpoint(path, method)) {
            return validateClientRole(exchange, chain);
        }

        return chain.filter(exchange);
    }

    private boolean shouldProtectEndpoint(String path, HttpMethod method) {
        return "/api/v1/applications".equals(path) && HttpMethod.POST.equals(method);
    }

    private Mono<Void> validateClientRole(ServerWebExchange exchange, WebFilterChain chain) {
        return extractTokenFromHeader(exchange)
                .flatMap(token -> validateTokenAndExtractEmail(token, exchange))
                .then(chain.filter(exchange))
                .onErrorResume(error -> {
                    log.warn("CLIENT_AUTHORIZATION_FAILED: {}", error.getMessage());
                    return unauthorizedResponse(exchange);
                });
    }

    private Mono<String> extractTokenFromHeader(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return Mono.just(authHeader.substring(7));
        }

        return Mono.error(new RuntimeException("No authorization token provided"));
    }

    private Mono<String> validateTokenAndExtractEmail(String token, ServerWebExchange exchange) {
        return Mono.fromCallable(() -> {
            String role = jwtValidationUtil.getRole(token);
            String email = jwtValidationUtil.getEmail(token);

            if (!CLIENT_ROLE.equals(role)) {
                throw new RuntimeException("Access denied - Client role required");
            }

            exchange.getAttributes().put("authenticatedEmail", email);
            log.info("CLIENT_AUTHORIZED email={}", email);

            return email;
        });
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
