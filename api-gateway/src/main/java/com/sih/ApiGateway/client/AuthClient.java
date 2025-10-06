package com.sih.ApiGateway.client;


import com.sanskar.sih.auth.ValidationResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", path = "/auth")
public interface AuthClient {
    @GetMapping("/validate")
    public ResponseEntity<ValidationResponseDTO> validateToken(@RequestHeader("Authorization") String token);
}
