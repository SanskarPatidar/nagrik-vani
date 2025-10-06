package com.sih.IssueService.client;

import com.sanskar.sih.citizen.CitizenProfileResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "citizen-service", path = "/citizen")
public interface CitizenClient {

    @GetMapping("/profile")
    ResponseEntity<CitizenProfileResponseDTO> getCitizenProfile(@RequestHeader("x-user-id") String userId);

    @PostMapping("/profile/internal-update")
    public ResponseEntity<CitizenProfileResponseDTO> internalUpdateProfile(
            @RequestBody CitizenProfileResponseDTO profile
    );

}