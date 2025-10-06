package com.sih.CitizenService.controller;

import com.sanskar.sih.citizen.CitizenProfileRequestDTO;
import com.sanskar.sih.citizen.CitizenProfileResponseDTO;
import com.sih.CitizenService.service.CitizenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/citizen/profile")
public class CitizenProfileController {

    @Autowired
    private CitizenService citizenService;

    @GetMapping("/me")
    public ResponseEntity<CitizenProfileResponseDTO> getMyProfile(@RequestHeader("x-user-id") String userId) {
        return ResponseEntity.ok(citizenService.getProfile(userId));
    }

    @PostMapping("/upsert") // internal auth use + external use
    public ResponseEntity<CitizenProfileResponseDTO> upsertProfile(
            @RequestBody CitizenProfileRequestDTO citizenProfileRequestDTO,
            @RequestHeader("x-user-id") String userId
    ) {
        return ResponseEntity.ok(citizenService.upsertProfile(citizenProfileRequestDTO, userId));
    }

    @GetMapping // for internal use
    public ResponseEntity<CitizenProfileResponseDTO> getCitizenProfile(@RequestHeader("x-user-id") String userId) {
        return ResponseEntity.ok(citizenService.findByUserId(userId));
    }

    @PostMapping("/internal-update") // for internal use
    public ResponseEntity<CitizenProfileResponseDTO> internalUpdateProfile(
            @RequestBody CitizenProfileResponseDTO profile
    ) {
        return ResponseEntity.ok(citizenService.internalUpdateProfile(profile));
    }
}
