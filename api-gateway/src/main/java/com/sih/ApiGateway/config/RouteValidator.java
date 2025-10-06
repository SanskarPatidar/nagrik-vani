package com.sih.ApiGateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/auth/register-citizen",
            "/auth/login",
            "/eureka",
            "/docs",
            "/v3/api-docs/**",        // <-- OpenAPI JSON
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}
