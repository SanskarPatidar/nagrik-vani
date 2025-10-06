package com.sih.AuthService.service;

import com.sih.AuthService.model.Token;
import com.sih.AuthService.model.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Utils {


    public String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public Token createToken(String userId, String token) {
        return Token.builder()
                .userId(userId)
                .token(token)
                .expired(false)
                .revoked(false)
                .deviceId(UUID.randomUUID().toString())
                .build();
    }

    public String getAuthenticatedUserId() {
        UserPrincipal userPrincipal = (UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userPrincipal.getId();
    }


}
