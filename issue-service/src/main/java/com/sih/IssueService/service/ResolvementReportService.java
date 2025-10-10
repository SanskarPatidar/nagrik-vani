package com.sih.IssueService.service;

import com.sanskar.common.exception.FeignCallDelegation;
import com.sanskar.common.exception.ForbiddenAccessException;
import com.sanskar.common.exception.NotFoundException;
import com.sanskar.sih.departmentadmin.DepartmentAdminProfileResponseDTO;
import com.sanskar.sih.issue.IssueStatus;
import com.sanskar.sih.issue.ResolvementRequestDTO;
import com.sanskar.sih.issue.ResolvementResponseDTO;
import com.sih.IssueService.client.DepartmentAdminClient;
import com.sih.IssueService.model.ResolvementReport;
import com.sih.IssueService.repository.IssueRepository;
import com.sih.IssueService.repository.ResolvementReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor // for final or @NonNull fields
public class ResolvementReportService {
    private final DepartmentAdminClient departmentAdminClient;
    private final ResolvementReportRepository resolvementReportRepository;
    private final IssueRepository issueRepository;

    public ResolvementResponseDTO resolveIssue(ResolvementRequestDTO requestDTO, String userId) {
        log.info("Resolving issue with IssueId: {}", requestDTO.getIssueId());
        var issue = issueRepository.findById(requestDTO.getIssueId())
                .orElseThrow(() -> new NotFoundException("Issue not found"));

        DepartmentAdminProfileResponseDTO adminProfile = FeignCallDelegation.execute(
                () -> departmentAdminClient.getDepartmentAdminById(userId)
        );

        if(adminProfile == null || !issue.getAssignedToId().equals(adminProfile.getId()))
            throw new ForbiddenAccessException("Issue not assigned to this department admin");

        issue.setStatus(IssueStatus.RESOLVED_NOT_VERIFIED);
        issueRepository.save(issue);

        return new ResolvementResponseDTO(
                resolvementReportRepository.save(
                        ResolvementReport.builder()
                                .id(UUID.randomUUID().toString())
                                .issueId(issue.getId())
                                .resolvedById(adminProfile.getId())
                                .resolvedAt(LocalDateTime.now())
                                .title(requestDTO.getTitle())
                                .description(requestDTO.getDescription())
                                .imageUrls(requestDTO.getImageUrls())
                                .build()
                )
        );

    }
}
