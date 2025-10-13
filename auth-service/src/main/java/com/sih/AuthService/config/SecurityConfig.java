package com.sih.AuthService.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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

/*
Request → JWT Filter → UsernamePasswordAuthenticationFilter → AuthorizationFilter → Controller
1. JWT Filter runs
-- Checks for JWT token
-- If missing/invalid, does NOT set Authentication
-- Passes request to next filter via filterChain.doFilter()

2. UsernamePasswordAuthenticationFilter runs
-- Checks for username/password (form login)
-- Since I disabled form login, this filter does nothing
-- Passes request to next filter

3. AuthorizationFilter runs
-- Checks the SecurityContext for an Authentication object
-- Sees that SecurityContext.getAuthentication() is null or not authenticated
-- Compares against our authorization rules: .anyRequest().authenticated()
-- Since no authentication exists but the endpoint requires .authenticated(), it rejects the request
-- Returns 403 Forbidden or 401 Unauthorized
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // for method-level annotations
@RequiredArgsConstructor
public class SecurityConfig {
    private final JWTFilter jwtFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // enough for both rest and ws
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // don’t use server-side sessions. The stateless policy ensures Spring won’t store authentication state between requests. // In a stateless JWT REST API, CSRF tokens are not needed because the server does not maintain user sessions. For stateless JWT authentication, we should “enable CORS and disable CSRF”
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // allow preflight
                        .requestMatchers( // manually from here or do from jwt filter
                                "/actuator/**",
                                "/auth/register-citizen",
                                "/auth/login",
                                "/auth/refresh",
                                "/oauth/google/callback",
                                "auth/docs/**",
                                "/auth/v3/api-docs",
                                "/docs",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(AbstractHttpConfigurer::disable) // disable basic authentication, as we are using JWT for authentication
                .formLogin(AbstractHttpConfigurer::disable) // disable form login, as we are using JWT for authentication
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // ensures our JWTFilter runs before Spring’s built-in UsernamePasswordAuthenticationFilter
                // if jwt filter validated the token, it sets Authentication in security context
                // UsernamePasswordAuthenticationFilter sees authentication already set, skips processing
                // UsernamePasswordAuthenticationFilter is for form login, which we are not using
                .build();
                // This way, any valid JWT in the request is parsed and the user’s identity is set before form-login or basic-auth filters run
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService); // there are other providers, provide our service class to DaoAuthProvider
        provider.setPasswordEncoder(passwordEncoder()); // encoder on password
        return provider;
    }

    @Bean // Spring Security 6 allows exposing the AuthenticationManager as a bean using the AuthenticationConfiguration
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    // Naturally, bean means getting hold of that object yourself

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}