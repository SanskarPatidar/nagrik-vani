package com.sih.AuthService.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ValidationResponseDTO {
    private String userId;
    private String username;
}
