package com.sih.ApiGateway.config;

import com.sanskar.sih.auth.ValidationResponseDTO;
import com.sih.ApiGateway.client.AuthClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class JWTFilter extends AbstractGatewayFilterFactory<JWTFilter.Config> {

    @Autowired
    @Lazy
    private AuthClient authClient;

    @Autowired
    private RouteValidator validator;

    @Autowired
    private RestTemplate template;
//    @Autowired
//    private JwtUtil jwtUtil;

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
                try {



                    System.out.println("calling Gateway Filter...!");

                    ResponseEntity<ValidationResponseDTO> response = authClient
                            .validateToken("Bearer " + authHeader);

                    ValidationResponseDTO result = response.getBody();


                    ServerHttpRequest modifiedRequest =  exchange.getRequest()
                            .mutate()
                            .header("x-user-id", result.getUserId())
                            .header("x-username", result.getUsername())
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build());

                } catch (Exception e) {
                    System.out.println("invalid access...!");
                    throw new RuntimeException("un authorized access to application");
                }
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {

    }

}