package co.com.crediya.auth.api.config;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Optional;
import java.util.UUID;

@Component
public class RequestIdFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestId = Optional.ofNullable(exchange.getRequest()
                        .getHeaders().getFirst("X-Request-Id"))
                .orElse(UUID.randomUUID().toString());

        return chain.filter(exchange)
                .contextWrite(Context.of("requestId", requestId))
                .doOnEach(signal -> MDC.put("requestId", requestId))
                .doFinally(signalType -> MDC.remove("requestId"));
    }
}
