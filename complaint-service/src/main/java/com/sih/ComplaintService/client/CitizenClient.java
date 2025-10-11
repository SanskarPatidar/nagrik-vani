package com.sih.ComplaintService.client;

import com.sanskar.sih.citizen.CitizenProfileResponseDTO;
import com.sanskar.sih.citizen.CitizenProfileInterchangeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "citizen-service", path = "/citizen")
public interface CitizenClient {

    @GetMapping("/profile")
    ResponseEntity<CitizenProfileResponseDTO> getCitizenProfile(@RequestHeader("x-user-id") String userId);

    @PostMapping("/profile/internal-update")
    ResponseEntity<CitizenProfileResponseDTO> internalUpdateProfile(
            @RequestBody CitizenProfileResponseDTO profile
    );

}