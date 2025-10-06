package com.sih.AuthService.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ValidationResponseDTO {
    private String userId;
    private String username;
}
