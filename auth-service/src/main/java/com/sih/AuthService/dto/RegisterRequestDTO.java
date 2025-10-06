package com.sih.AuthService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequestDTO {
//    @NotBlank(message = "Username cannot be blank")
//    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
//    @NotBlank(message = "Password cannot be blank")
//    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
//    @NotBlank(message = "Email cannot be blank")
//    @Email
    private String email;
}
