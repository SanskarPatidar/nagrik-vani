package com.sih.AuthService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthCodeExchangeRequestDTO {
//    @NotBlank(message = "Code cannot be blank")
    private String code;
    private String redirectUri;
}
