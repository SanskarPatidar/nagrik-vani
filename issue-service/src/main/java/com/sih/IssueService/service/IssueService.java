package com.sih.IssueService.service;

import com.sanskar.sih.complaint.ComplaintRequestDTO;
import com.sanskar.sih.departmentadmin.DepartmentAdminProfileResponseDTO;
import com.sanskar.sih.issue.*;
import com.sih.IssueService.client.ComplaintClient;
import com.sih.IssueService.client.DepartmentAdminClient;
import com.sih.IssueService.dto.*;
import com.sih.IssueService.model.Issue;
import com.sih.IssueService.model.ResolvementReport;
import com.sih.IssueService.repository.IssueRepository;
import com.sih.IssueService.repository.ResolvementReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ComplaintClient complaintClient;

    @Autowired
    private ResolvementReportRepository resolvementReportRepository;

    public Page<IssueSearchResponseDTO> getAllIssues(Pageable pageable) {
        log.info("Fetching all issues");
        return issueRepository.findAll(pageable)
                .map(IssueSearchResponseDTO::new);
    }

    public Page<IssueSearchResponseDTO> searchIssues(IssueSearchRequestDTO requestDTO, Pageable pageable) {
        log.info("Searching issues");
        return issueRepository
                .searchIssues(requestDTO, pageable)
                .map(IssueSearchResponseDTO::new);
    }

    public IssueSearchResponseDTO getIssueById(String issueId) {
        log.info("Getting issue by id: {}", issueId);
        return issueRepository.findById(issueId)
                .map(IssueSearchResponseDTO::new)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
    }

    public IssueInterchangeDTO findByTypeAndWithinRadius(String type, double centerLat, double centerLon, double radiusInMeters) {
        Issue issue = issueRepository.findByTypeAndWithinRadius(type, centerLat, centerLon, radiusInMeters);
        return issue == null ? null : new IssueInterchangeDTO(issue);
    }

    public IssueInterchangeDTO createIssue(ComplaintRequestDTO request) {
        return new IssueInterchangeDTO(
                issueRepository.save(
                        Issue.builder()
                                .id(UUID.randomUUID().toString())
                                .issuedAt(LocalDateTime.now())
                                .title(request.getTitle())
                                .description(request.getDescription())
                                .imageUrl(request.getImageUrls().get(0))
                                .location(new GeoJsonPoint(request.getLocationLon(), request.getLocationLat()))
                                .city(request.getCity())
                                .state(request.getState())
                                .country(request.getCountry())
                                .type(request.getType())
                                .status(IssueStatus.OPEN_NOT_ASSIGNED)
                                .likes(0L)
                                .build()
                )
        );
    }

    public void ackIssue(String issueId, String deptId) {
        log.info("Acknowledging issue with IssueId: {}", issueId);
        var issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        if(!issue.getAssignedToId().equals(deptId))
            throw new RuntimeException("Issue not assigned to this department");

        issue.setStatus(IssueStatus.ACKNOWLEDGED);
        issueRepository.save(issue);

        complaintClient.acknowledgeComplaints(issueId);
    }

    public void likeIssue(String issueId) {
        log.info("Liking issue with IssueId: {}", issueId);
        var issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        issue.setLikes(issue.getLikes() + 1);
        issueRepository.save(issue);
    }

    public IssueInterchangeDTO getIssueByResolvementReportId(String resId) {
        log.info("Getting issue by resolving report id: {}", resId);

        var report = resolvementReportRepository.findById(resId)
                .orElseThrow(() -> new RuntimeException("Resolvement Report not found"));

        return new IssueInterchangeDTO(
                issueRepository.findById(report.getIssueId())
                        .orElseThrow(() -> new RuntimeException("Issue not found"))
        );
    }
}
