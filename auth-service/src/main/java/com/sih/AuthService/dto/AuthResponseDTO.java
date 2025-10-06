package com.sih.AuthService.dto;

import com.sih.AuthService.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    private String accessToken;
    private String refreshToken;
    private String username;
    private String email;
    private String userId;
    private String deviceId;
    private List<Role> roles;
}
