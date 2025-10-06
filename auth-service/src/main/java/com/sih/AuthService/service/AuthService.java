package com.sih.AuthService.service;


import com.sanskar.sih.citizen.CitizenProfileRequestDTO;
import com.sanskar.sih.citizen.CitizenProfileResponseDTO;
import com.sih.AuthService.client.CitizenClient;
import com.sih.AuthService.dto.*;
import com.sih.AuthService.model.Role;
import com.sih.AuthService.model.Token;
import com.sih.AuthService.model.User;
import com.sih.AuthService.model.UserPrincipal;
import com.sih.AuthService.repository.UserRepository;
import com.sih.AuthService.repository.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService { // Main JWT business logic service class, MyUserDetailService is just for mapping

    @Autowired
    private JWTGenerator jwtGenerator;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    AuthenticationManager authManager;
    /*
    Raw username and password from request body
    Spring creates a UsernamePasswordAuthenticationToken
    Spring loads user from DB using username via MyUserDetailsService
    Spring compares raw password with hashed password in DB
    Authenticated token created with authorities and saved in SecurityContext
     */

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private Utils utils;

    @Autowired
    private CitizenClient citizenClient;

    public AuthResponseDTO citizenRegister(RegisterRequestDTO registerRequestDTO) {
        if (repo.existsByUsername(registerRequestDTO.getUsername())) {
            throw new RuntimeException("Username already exists.");
        }
        if(repo.existsByEmail(registerRequestDTO.getEmail())) {
            throw new RuntimeException("Email already exists.");
        }

        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .username(registerRequestDTO.getUsername())
                .password(encoder.encode(registerRequestDTO.getPassword()))
                .isPasswordSet(true)
                .email(registerRequestDTO.getEmail())
                .build();
        var savedUser = repo.save(user);

        String accessToken = jwtGenerator.generateAccessToken(user.getUsername());
        Token accessTokenEntity = utils.createToken(savedUser.getId(), accessToken);

        String refreshToken = jwtGenerator.generateRefreshToken(user.getUsername());

        tokenRepository.save(accessTokenEntity);
        refreshTokenService.storeRefreshToken(savedUser.getId(), accessTokenEntity.getDeviceId(), refreshToken); // Store refresh token in Redis

        // Creating citizen profile
        CitizenProfileRequestDTO body = new CitizenProfileRequestDTO();
        body.setFullName(user.getUsername());
        CitizenProfileResponseDTO result = citizenClient.upsertProfile(body, user.getId()).getBody();

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .userId(savedUser.getId())
                .deviceId(accessTokenEntity.getDeviceId())
                .roles(List.of(Role.CITIZEN))
                .build();
    }

//    public AuthResponseDTO departmentAdminRegister() {
//
//    }
//
//    public AuthResponseDTO departmentStaffRegister() {
//
//    }

    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
        try {
            // Calls UserDetailsService which was passed to AuthProvider in SecurityConfig
            // Authentication Object contains principal(UserDetails)
            // Using this approach when we don't trust user's details
            // call it DELEGATED AUTHENTICATION
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getLoginString(), loginRequestDTO.getPassword())
            );
            // This will do all validations of username and password

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            // no revoke logic as we can't blindly trust devic id
            String accessToken = jwtGenerator.generateAccessToken(userPrincipal.getUsername());
            Token accessTokenEntity = utils.createToken(userPrincipal.getId(), accessToken);

            String refreshToken = jwtGenerator.generateRefreshToken(userPrincipal.getUsername());

            tokenRepository.save(accessTokenEntity);
            refreshTokenService.storeRefreshToken(userPrincipal.getId(), accessTokenEntity.getDeviceId(), refreshToken);

            return AuthResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .username(userPrincipal.getUsername())
                    .email(userPrincipal.getEmail())
                    .userId(userPrincipal.getId())
                    .deviceId(accessTokenEntity.getDeviceId())
                    .roles(userPrincipal.getRoles())
                    .build();

        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid username or password.");
        }
    }

    private void revokeAllUserTokens(User user, String deviceId) {
        var validUserTokens = tokenRepository.findValidTokensByUserIdAndDeviceId(user.getId(), deviceId);
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        refreshTokenService.deleteRefreshToken(user.getId(), deviceId);
        tokenRepository.saveAll(validUserTokens);
    }

    public AuthResponseDTO refresh(String deviceId, HttpServletRequest request) throws IOException {
        // JWT filter like validation
        String raw = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (raw == null || !raw.startsWith("Bearer ")) {
            throw new BadCredentialsException("Missing or malformed token.");
        }
        String refreshToken = raw.substring(7);
        String username = jwtGenerator.extractUserName(refreshToken);
        if (username != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            User user = repo.findByUsername(username)
                    .orElseThrow(() -> new BadCredentialsException("User not found."));
            boolean isTokenValid = refreshTokenService.isRefreshTokenValid(user.getId(), deviceId, refreshToken); // this also validates for deviceId

            if (jwtGenerator.validateToken(refreshToken, userDetails) && isTokenValid) {
                revokeAllUserTokens(user, deviceId);

                String accessToken = jwtGenerator.generateAccessToken(username);
                Token accessTokenEntity = utils.createToken(user.getId(), accessToken);
                refreshToken = jwtGenerator.generateRefreshToken(username);

                accessTokenEntity.setDeviceId(deviceId);

                tokenRepository.save(accessTokenEntity);
                refreshTokenService.storeRefreshToken(user.getId(), deviceId, refreshToken);

                return AuthResponseDTO.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .userId(user.getId())
                        .deviceId(deviceId)
                        .roles(user.getRoles())
                        .build();
            }
        }
        throw new BadCredentialsException("Invalid refresh token.");
    }

    public ValidationResponseDTO validateToken() {
        return ValidationResponseDTO.builder()
                .userId(utils.getAuthenticatedUserId())
                .username(utils.getAuthenticatedUsername())
                .build();
    }
}