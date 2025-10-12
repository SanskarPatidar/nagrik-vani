package com.sih.ApiGateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    // handle ** with care
    public static final List<String> openApiEndpoints = List.of(
            "/auth/register-citizen",
            "/auth/login",
            "/auth/refresh",
            "/eureka",
            "/docs",
            "/v3/api-docs",
            "/swagger-ui.html",
            "/swagger-ui",
            "/webjars"
    );

    // skip filter for these endpoints
    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}
