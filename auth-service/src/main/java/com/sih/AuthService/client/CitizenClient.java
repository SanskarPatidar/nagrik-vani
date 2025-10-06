package com.sih.AuthService.client;

import com.sanskar.sih.citizen.CitizenProfileInterchangeDTO;
import com.sanskar.sih.citizen.CitizenProfileRequestDTO;
import com.sanskar.sih.citizen.CitizenProfileResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "citizen-service", path = "/citizen")
public interface CitizenClient {

    @PostMapping("/profile/upsert")
    public ResponseEntity<CitizenProfileResponseDTO> upsertProfile(
            @RequestBody CitizenProfileRequestDTO citizenProfileRequestDTO,
            @RequestHeader("x-user-id") String userId
    );

}