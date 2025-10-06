package com.sih.ApiGateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GatewayConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("http://localhost:5173");
        corsConfig.addAllowedOrigin("http://127.0.0.1:5173");
        corsConfig.addAllowedOrigin("http://192.168.249.108:5173");
        corsConfig.addAllowedOrigin("http://192.168.249.108:5173"); // ashutosh
        corsConfig.addAllowedOrigin("http://192.168.249.21:3000");
        corsConfig.addAllowedOrigin("http://192.168.185.21:3000"); // faeez
//        corsConfig.addAllowedOrigin("*");
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);

        corsConfig.addAllowedOrigin("capacitor://localhost");
        corsConfig.addAllowedOrigin("http://localhost");
        corsConfig.addAllowedOrigin("file://");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return new CorsWebFilter(source);
    }
}