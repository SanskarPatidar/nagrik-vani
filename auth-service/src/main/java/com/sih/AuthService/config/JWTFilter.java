package com.sih.AuthService.config;


import com.sih.AuthService.repository.TokenRepository;
import com.sih.AuthService.service.JWTGenerator;
import com.sih.AuthService.service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
QUESTION: Why need to authenticate the token in SecurityContextHolder, when we are validating the token ourselves?
ANSWER: Hold this validated token to later access in code and no need to call repository to access info which are already present in the token.
 */

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter { // Ensures it runs once per request
    private final JWTGenerator jwtGenerator;
    private final TokenRepository tokenRepository;
    private final MyUserDetailsService myUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // endpoints mentioned in SecurityConfig as permitAll() still gets processed through filters
        // can override shouldNotFilter() function of OncePerRequestFilter to skip filtering for specific endpoints

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtGenerator.extractUserName(token);
        }
        else log.info("Bearer token is null");

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { // Prevents re-authentication, This ensures we only authenticate if not already done for this request.
            UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);
            var isTokenValid = tokenRepository.findByToken(token)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);
            if (jwtGenerator.validateToken(token, userDetails) && isTokenValid) {
                // call it MANUAL AUTHENTICATION
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); // using this approach when we trust user's details (not at time of login)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken); // This sets the authentication in the SecurityContext, allowing Spring Security to recognize the user for this request.
            }
            else log.info("Invalid token: {}", token);
        }
        else log.info("Username is null or context is not null, username: {}, context: {}", username, SecurityContextHolder.getContext().getAuthentication());


        filterChain.doFilter(request, response); // Continue with the filter chain, allowing the request to proceed to the next filter or endpoint
    }
}

/*
QUESTION: What happens for invalid token right now?
ANSWER:
If the token is invalid/expired/malformed:
No authentication is set in the SecurityContext
The request proceeds
If the endpoint is public, it works
If the endpoint is protected (needs authentication), Spring Security will eventually respond with:

401 Unauthorized (if unauthenticated)
403 Forbidden (if authenticated but lacks permission)
 */