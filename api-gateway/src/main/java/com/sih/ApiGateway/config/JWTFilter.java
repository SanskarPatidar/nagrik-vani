package com.sih.ApiGateway.config;

import com.sanskar.sih.auth.ValidationResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JWTFilter extends AbstractGatewayFilterFactory<JWTFilter.Config> {

    @Autowired
    private RouteValidator validator;

    ;
//    @Autowired
//    private JwtUtil jwtUtil;

    @Autowired
    private WebClient.Builder webClientBuilder; // this is reactive environment, so use webclient or rest template

    public JWTFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {

            if (
                    exchange.getRequest().getPath().toString().contains("/v3/api-docs") ||
                            exchange.getRequest().getPath().toString().contains("/swagger-ui") ||
                            exchange.getRequest().getPath().toString().contains("/webjars") ||
                            exchange.getRequest().getPath().toString().contains("/docs")
            ) {
                return chain.filter(exchange);
            }


            if (validator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                return webClientBuilder.build().get()
                        .uri("lb://auth-service/auth/validate")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + authHeader)
                        .retrieve()
                        .bodyToMono(ValidationResponseDTO.class)
                        .flatMap(result -> {
                            // If successful, add headers to the request and pass it down the chain
                            ServerHttpRequest modifiedRequest = exchange.getRequest()
                                    .mutate()
                                    .header("x-user-id", result.getUserId())
                                    .header("x-username", result.getUsername())
                                    .build(); // The .build() method is CRITICAL

                            return chain.filter(exchange.mutate().request(modifiedRequest).build());
                        })
                        .onErrorResume(e -> {
                            System.out.println("Token validation failed: " + e.getMessage());
                            return onError(exchange, "Unauthorized access to application", HttpStatus.UNAUTHORIZED);
                        });
            }
            return chain.filter(exchange);
        });
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    public static class Config {

    }

}