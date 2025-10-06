package com.sih.AuthService.controller;



import com.sih.AuthService.dto.AuthResponseDTO;
import com.sih.AuthService.dto.LoginRequestDTO;
import com.sih.AuthService.dto.RegisterRequestDTO;
import com.sih.AuthService.dto.ValidationResponseDTO;
import com.sih.AuthService.service.AuthService;
import com.sih.AuthService.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService userSecurityService;

    @Autowired
    private LogoutService logoutService;

    @PostMapping("/register-citizen")
    public ResponseEntity<AuthResponseDTO> registerCitizen(@RequestBody RegisterRequestDTO registerRequestDTO){
        System.out.println("Registering citizen: " + registerRequestDTO);
        return ResponseEntity.ok(userSecurityService.citizenRegister(registerRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO){
        return ResponseEntity.ok(userSecurityService.login(loginRequestDTO));
    }

    @PostMapping("/refresh/{deviceId}")
    public ResponseEntity<AuthResponseDTO> refresh(@PathVariable String deviceId, HttpServletRequest request) throws IOException {
        return ResponseEntity.ok(userSecurityService.refresh(deviceId, request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logoutService.logout(request, response, authentication);
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/validate")
    public ResponseEntity<ValidationResponseDTO> validateToken() {
        return ResponseEntity.ok(userSecurityService.validateToken());
    }

}
