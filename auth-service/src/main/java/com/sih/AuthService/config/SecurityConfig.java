package com.sih.AuthService.config;

import org.apache.catalina.filters.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // for method-level annotations
public class SecurityConfig {


    @Autowired
    private JWTFilter jwtFilter;

    @Autowired
    private LogoutHandler logoutHandler; // custom logout handler

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // enough for both rest and ws
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // don’t use server-side sessions. The stateless policy ensures Spring won’t store authentication state between requests. // In a stateless JWT REST API, CSRF tokens are not needed because the server does not maintain user sessions. As one guide explains, for stateless JWT authentication, we should “enable CORS and disable CSRF”
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // allow preflight
                        .requestMatchers(
                                "/auth/register-citizen", // manually from here or do from jwt filter
                                "/auth/login",
                                "/oauth/google/callback",
                                "auth/docs/**",
                                "/auth/v3/api-docs",
                                "/docs",
                                "/v3/api-docs/**",        // <-- OpenAPI JSON
                                "/swagger-ui.html",
                                "/swagger-ui/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(AbstractHttpConfigurer::disable) // disable basic authentication, as we are using JWT for authentication
                .formLogin(AbstractHttpConfigurer::disable) // disable form login, as we are using JWT for authentication
                // disable form login, as we are using JWT for authentication
//                .addFilterAfter(jwtFilter, CorsFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // ensures our JWTFilter runs before Spring’s built-in UsernamePasswordAuthenticationFilter
                .build();
                // This way, any valid JWT in the request is parsed and the user’s identity is set before form-login or basic-auth filters run
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.addAllowedOrigin("http://192.168.249.108:5173"); // Friend's IP
//        config.addAllowedOrigin("http://192.168.249.47:5173");  // Your IP
//        config.addAllowedOrigin("http://localhost:5173");       // Local development
//        config.addAllowedOrigin("http://127.0.0.1:5173");       // Alternative localhost
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        config.setAllowedHeaders(List.of("*"));
//        config.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService); // there are other providers, // provide our service class to DaoAuthProvider
        provider.setPasswordEncoder(passwordEncoder()); // encoder on password
        return provider;
    }

    @Bean //Spring Security 6 allows exposing the AuthenticationManager as a bean using the AuthenticationConfiguration
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    // Naturally, bean means getting hold of that object yourself

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }



}