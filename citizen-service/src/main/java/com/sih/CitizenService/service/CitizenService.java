package com.sih.CitizenService.service;

import com.sanskar.sih.citizen.CitizenProfileRequestDTO;
import com.sanskar.sih.citizen.CitizenProfileResponseDTO;
import com.sih.CitizenService.model.CitizenProfile;
import com.sih.CitizenService.repository.CitizenProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.sanskar.sih.citizen.CitizenProfileInterchangeDTO;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class CitizenService {

    @Autowired
    private CitizenProfileRepository citizenProfileRepository;

    public CitizenProfileResponseDTO getProfile(String userId) {
        log.info("Getting profile for userId: " + userId);
        CitizenProfile profile = citizenProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Citizen profile not found."));
        return new CitizenProfileResponseDTO(profile);
    }

    public CitizenProfileResponseDTO upsertProfile(CitizenProfileRequestDTO citizenProfileRequestDTO, String userId) {
        log.info("Upserting profile for userId: " + userId);
        CitizenProfile profile = citizenProfileRepository.findByUserId(userId)
                .orElse(new CitizenProfile());

        profile.setId(UUID.randomUUID().toString());
        profile.setUserId(userId);
        if(profile.getTotalComplaints() == null)profile.setTotalComplaints(0L);
        if(profile.getTotalComments() == null)profile.setTotalComments(0L);
        if(profile.getTotalFeedbacks() == null)profile.setTotalFeedbacks(0L);
        if(profile.getCommunityScore() == null)profile.setCommunityScore(0.0);

        if(citizenProfileRequestDTO.getFullName() != null)profile.setFullName(citizenProfileRequestDTO.getFullName());
        if(citizenProfileRequestDTO.getAddress() != null)profile.setAddress(citizenProfileRequestDTO.getAddress());
        if(citizenProfileRequestDTO.getProfileImageUrl() != null)profile.setProfileImageUrl(citizenProfileRequestDTO.getProfileImageUrl());
        return new CitizenProfileResponseDTO(citizenProfileRepository.save(profile));
    }

    public CitizenProfileResponseDTO findByUserId(String userId) {
        log.info("Finding citizen profile for userId: " + userId);
        return new CitizenProfileResponseDTO(
                citizenProfileRepository.findByUserId(userId)
                        .orElseThrow(() -> new RuntimeException("Citizen profile not found."))
        );
    }

    public CitizenProfileResponseDTO internalUpdateProfile(CitizenProfileResponseDTO profile) {
        log.info("Updating citizen profile for userId: " + profile.getUserId());
        CitizenProfile existingProfile = citizenProfileRepository.findByUserId(profile.getUserId())
                .orElseThrow(() -> new RuntimeException("Citizen profile not found."));

        if(profile.getFullName() != null)existingProfile.setFullName(profile.getFullName());
        if(profile.getAddress() != null)existingProfile.setAddress(profile.getAddress());
        if(profile.getProfileImageUrl() != null)existingProfile.setProfileImageUrl(profile.getProfileImageUrl());
        if(profile.getTotalComplaints() != null)existingProfile.setTotalComplaints(existingProfile.getTotalComplaints() + profile.getTotalComplaints());
        if(profile.getTotalComments() != null)existingProfile.setTotalComments(existingProfile.getTotalComments() + profile.getTotalComments());
        if(profile.getTotalFeedbacks() != null)existingProfile.setTotalFeedbacks(existingProfile.getTotalFeedbacks() + profile.getTotalFeedbacks());

        Long complaints = existingProfile.getTotalComplaints();
        Long comments = existingProfile.getTotalComments();
        Long feedbacks = existingProfile.getTotalFeedbacks();

        Long saturationConstant = 100L; // Saturation formula: x / (x + k) -> 1 as x -> infinity

        Double complaintScore = (double) complaints / (complaints + saturationConstant);
        Double commentScore = (double) comments / (comments + saturationConstant);
        Double feedbackScore = (double) feedbacks / (feedbacks + saturationConstant);

        double score = (((4 * complaintScore) + (2 * commentScore) + (4 * feedbackScore)) / 10.0) * 10.0; // Scale to 0-10

        existingProfile.setCommunityScore(score);
        log.info("Saved citizen profile: " + existingProfile);
        return new CitizenProfileResponseDTO(citizenProfileRepository.save(existingProfile));
    }
}
