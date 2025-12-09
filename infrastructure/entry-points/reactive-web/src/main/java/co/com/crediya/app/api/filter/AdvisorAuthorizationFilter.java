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
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class AdvisorAuthorizationFilter implements WebFilter {

    private static final String ADVISOR_ROLE = "ROLE_ADVISOR";
    private final JwtValidationUtil jwtValidationUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        HttpMethod method = exchange.getRequest().getMethod();

        return shouldProtectEndpoint(path, method)
                ? validateAdvisorRole(exchange, chain)
                : chain.filter(exchange);
    }

    private boolean shouldProtectEndpoint(String path, HttpMethod method) {
        return "/api/v1/applications".equals(path) && HttpMethod.GET.equals(method);
    }

    private Mono<Void> validateAdvisorRole(ServerWebExchange exchange, WebFilterChain chain) {
        return extractTokenFromHeader(exchange)
                .flatMap(this::validateTokenAndRole)
                .then(chain.filter(exchange))
                .onErrorResume(error -> {
                    log.warn("ADVISOR_AUTHORIZATION_FAILED: {}", error.getMessage());
                    return unauthorizedResponse(exchange);
                });
    }

    private Mono<String> extractTokenFromHeader(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7))
                .switchIfEmpty(Mono.error(new RuntimeException("No authorization token provided")));
    }

    private Mono<Void> validateTokenAndRole(String token) {
        return Mono.fromCallable(() -> jwtValidationUtil.getRole(token))
                .filter(ADVISOR_ROLE::equals)
                .switchIfEmpty(Mono.error(new RuntimeException("Access denied - Advisor role required")))
                .doOnNext(roleId -> log.info("ADVISOR_AUTHORIZED roleId={}", roleId))
                .then();
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }
}
