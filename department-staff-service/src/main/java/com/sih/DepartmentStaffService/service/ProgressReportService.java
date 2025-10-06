package com.sih.DepartmentStaffService.service;

import com.sanskar.sih.departmentadmin.TaskCreateResponseDTO;
import com.sanskar.sih.departmentstaff.ProgressReportCreateRequestDTO;
import com.sanskar.sih.departmentstaff.ProgressReportCreateResponseDTO;
import com.sih.DepartmentStaffService.client.DepartmentAdminClient;
import com.sih.DepartmentStaffService.model.DepartmentStaffProfile;
import com.sih.DepartmentStaffService.model.ProgressReport;
import com.sih.DepartmentStaffService.repository.DepartmentStaffProfileRepository;
import com.sih.DepartmentStaffService.repository.ProgressReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class ProgressReportService {

    @Autowired
    private ProgressReportRepository progressReportRepository;

    @Autowired
    private DepartmentStaffProfileRepository departmentStaffProfileRepository;

    @Autowired
    private DepartmentAdminClient departmentAdminClient;

    public ProgressReportCreateResponseDTO createProgressReport(ProgressReportCreateRequestDTO requestDTO, String userId) {
        log.info("Creating progress report for userId: {}", userId);
        DepartmentStaffProfile profile = departmentStaffProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Department staff profile not found for userId: " + userId));

        TaskCreateResponseDTO task = departmentAdminClient.getTaskById(requestDTO.getTaskId()).getBody();

        if(!task.getAssignedToId().equals(profile.getId()))
            throw new RuntimeException("Task not assigned to this staff");

        ProgressReport report = ProgressReport.builder()
                .id(UUID.randomUUID().toString())
                .taskId(requestDTO.getTaskId())
                .reportedById(profile.getId())
                .reportedAt(LocalDateTime.now())
                .reportTitle(requestDTO.getReportTitle())
                .reportDescription(requestDTO.getReportDescription())
                .imageUrls(requestDTO.getImageUrls())
                .build();

        return new ProgressReportCreateResponseDTO(progressReportRepository.save(report));
    }

    public Page<ProgressReportCreateResponseDTO> getProgressReportByTaskId(String taskId, Pageable pageable) {
        log.info("Getting progress report for taskId: {}", taskId);
        return progressReportRepository.findAllByTaskId(taskId, pageable)
                .map(ProgressReportCreateResponseDTO::new);
    }

}
