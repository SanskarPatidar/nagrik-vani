package com.sih.ApiGateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanskar.common.exception.ErrorResponse;
import com.sanskar.sih.auth.ValidationResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@Slf4j
public class JWTFilter extends AbstractGatewayFilterFactory<JWTFilter.Config> {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RouteValidator validator;

    @Autowired
    private WebClient.Builder webClientBuilder; // this is reactive environment, so use webclient or rest template

    public JWTFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {

            if (validator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return onError(exchange, "No Authorization header found", HttpStatus.UNAUTHORIZED);
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
                            log.warn("Token validation call failed with message: {}", e.getMessage());

                            if (e instanceof WebClientResponseException ex) {
                                // it was a 4xx or 5xx
                                HttpStatus status = (HttpStatus) ex.getStatusCode();
                                if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN) {
                                    log.error("Authorization failed: Token is invalid or expired.");
                                    return onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
                                } else {
                                    log.error("Downstream service auth-service returned a server error: {}", status);
                                    return onError(exchange, "Downstream service is unavailable", HttpStatus.SERVICE_UNAVAILABLE);
                                }
                            } else if (e instanceof WebClientRequestException) {
                                // network error
                                log.error("Network error while contacting auth-service.");
                                return onError(exchange, "Downstream service is unreachable", HttpStatus.SERVICE_UNAVAILABLE);
                            } else {
                                // This is a catch-all for other unexpected errors
                                log.error("An unexpected error occurred during token validation.");
                                return onError(exchange, "An internal error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
                            }
                        });
            }
            return chain.filter(exchange);
        });
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(message)
                .path(exchange.getRequest().getPath().toString())
                .build();

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.warn("Error serializing error response", e);
            return response.setComplete();
        }
    }

    public static class Config {}

}