// infrastructure/entry-points/reactive-web/src/.../filter/JwtContextFilter.java

package co.com.crediya.app.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Component
public class JwtContextFilter implements WebFilter {

    private static final String JWT_CONTEXT_KEY = "JWT_TOKEN";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Quitar "Bearer "

            // Guardar JWT en Reactor Context
            return chain.filter(exchange)
                    .contextWrite(Context.of(JWT_CONTEXT_KEY, token));
        }

        // Si no hay JWT, continuar sin agregar al context
        return chain.filter(exchange);
    }
}
