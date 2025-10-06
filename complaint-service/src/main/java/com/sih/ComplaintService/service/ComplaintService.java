package com.sih.ComplaintService.service;

import com.sanskar.sih.citizen.CitizenProfileInterchangeDTO;
import com.sanskar.sih.citizen.CitizenProfileResponseDTO;
import com.sanskar.sih.complaint.ComplaintRequestDTO;
import com.sanskar.sih.complaint.ComplaintResponseDTO;
import com.sanskar.sih.complaint.ComplaintStatus;
import com.sanskar.sih.issue.IssueInterchangeDTO;
import com.sih.ComplaintService.client.CitizenClient;
import com.sih.ComplaintService.client.IssueClient;
import com.sih.ComplaintService.model.Complaint;
import com.sih.ComplaintService.repository.ComplaintRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class ComplaintService {

    @Autowired
    private CitizenClient citizenClient;

    @Autowired
    private IssueClient issueClient;

    @Autowired
    private ComplaintRepository complaintRepository;

    public ComplaintResponseDTO postComplaint(ComplaintRequestDTO request, String userId) {
        log.info("Posting complaint for userId: {}", userId);
        // got citizen profile
        CitizenProfileResponseDTO profile = CitizenProfileResponseDTO.builder()
                .userId(userId)
                .totalComplaints(1L)
                .build();

        profile = citizenClient.internalUpdateProfile(profile).getBody();

        // map complaint to issue
        IssueInterchangeDTO issue = issueClient.findByTypeAndWithinRadius(
                request.getType(),
                request.getLocationLat(),
                request.getLocationLon(),
                50.0
                ).getBody();

        // if issue is null, create a new issue
        if(issue == null) {
            issue = issueClient.createIssue(request).getBody();
        }

        log.info("Mapped Issue id: {}", userId);

        // create complaint
        Complaint complaint = Complaint.builder()
                .id(UUID.randomUUID().toString())
                .citizenId(profile.getId())
                .issueId(issue.getId())
                .createdAt(LocalDateTime.now())
                .title(request.getTitle())
                .description(request.getDescription())
                .locationLat(request.getLocationLat())
                .locationLon(request.getLocationLon())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .type(request.getType())
                .complaintStatus(ComplaintStatus.PENDING)
                .isPublic(request.isPublic())
                .imageUrls(request.getImageUrls())
                .build();
        return new ComplaintResponseDTO(complaintRepository.save(complaint));
    }

    public Page<ComplaintResponseDTO> getMyComplaints(Pageable pageable, String userId) {
        log.info("Fetching complaints for userId: " + userId);
        CitizenProfileResponseDTO profile = citizenClient.getCitizenProfile(userId).getBody();

        return complaintRepository
                .findAllByCitizenId(profile.getId(), pageable)
                .map(ComplaintResponseDTO::new);
    }

    public Page<ComplaintResponseDTO> getAllComplaintsByIssueId(Pageable pageable, String issueId) {
        log.info("Fetching complaints for issueId: " + issueId);
        return complaintRepository
                .findAllByIssueId(issueId, pageable)
                .map(complaint -> {
                    log.info("Found complaint with id: {}", complaint.getId());
                    ComplaintResponseDTO dto = new ComplaintResponseDTO(complaint);
                    if (!dto.isPublic()) {
                        dto.setCitizenId("In your dreams bro");
                    }
                    return dto;
                });
    }

    public void acknowledgeComplaints(String issueId) {
        log.info("Acknowledging complaints for issueId: " + issueId);
        complaintRepository.acknowledgeComplaints(issueId);
    }
}
