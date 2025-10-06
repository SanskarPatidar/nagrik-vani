package com.sih.ComplaintService.service;

import com.netflix.discovery.converters.Auto;
import com.sanskar.sih.citizen.CitizenProfileResponseDTO;
import com.sanskar.sih.issue.IssueInterchangeDTO;
import com.sih.ComplaintService.client.CitizenClient;
import com.sih.ComplaintService.client.IssueClient;
import com.sih.ComplaintService.dto.FeedbackRequestDTO;
import com.sih.ComplaintService.model.Complaint;
import com.sih.ComplaintService.model.Feedback;
import com.sih.ComplaintService.repository.ComplaintRepository;
import com.sih.ComplaintService.repository.FeedbackRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private IssueClient issueClient;

    @Autowired
    private CitizenClient citizenClient;

    public void upsertFeedback(FeedbackRequestDTO request, String userId) {
        Complaint complaint = complaintRepository.findById(request.getComplaintId())
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + request.getComplaintId()));

        IssueInterchangeDTO issue = issueClient.getIssueByResolvementReportId(request.getResolvementReportId()).getBody();

        if(!issue.getId().equals(complaint.getIssueId()))
            throw new RuntimeException("Resolvement Report does not belong to the Complaint's Issue");

        Feedback feedback = feedbackRepository.findByComplaintIdAndResolvementReportId(request.getComplaintId(), request.getResolvementReportId())
                .orElse(
                        Feedback.builder()
                                .id(UUID.randomUUID().toString())
                                .complaintId(request.getComplaintId())
                                .resolvementReportId(request.getResolvementReportId())
                                .build()
                );

        feedback.setSubmittedAt(LocalDateTime.now());
        feedback.setSatisfied(request.isSatisfied());
        feedback.setDescription(request.getDescription());

        CitizenProfileResponseDTO citizenProfile = CitizenProfileResponseDTO.builder()
                .userId(userId)
                .totalFeedbacks(1L)
                .build();
        citizenProfile = citizenClient.internalUpdateProfile(citizenProfile).getBody();



    }
}
