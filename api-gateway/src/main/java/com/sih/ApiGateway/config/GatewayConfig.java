package com.sih.ApiGateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GatewayConfig {

    // Origin: protocol + host + port
    // CORS config need if origin is different from backend server
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();


        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true); // incompatible with corsConfig.addAllowedOrigin("*")

        corsConfig.addAllowedOrigin("capacitor://localhost"); // for mobile apps using capacitor framework
        corsConfig.addAllowedOrigin("http://localhost");
        corsConfig.addAllowedOrigin("file://");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return new CorsWebFilter(source);
    }
}