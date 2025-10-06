package com.sih.DepartmentStaffService.controller;

import com.sanskar.sih.departmentstaff.ProgressReportCreateRequestDTO;
import com.sanskar.sih.departmentstaff.ProgressReportCreateResponseDTO;
import com.sih.DepartmentStaffService.dto.PageResponse;
import com.sih.DepartmentStaffService.service.ProgressReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("department-staff/progress-report")
public class ProgressReportController {

    @Autowired
    private ProgressReportService progressReportService;

    @PostMapping("/create")
    public ResponseEntity<ProgressReportCreateResponseDTO> createProgressReport(
            @RequestBody ProgressReportCreateRequestDTO requestDTO,
            @RequestHeader("x-user-id") String userId
    ) {
        return ResponseEntity.ok(progressReportService.createProgressReport(requestDTO, userId));
    }

    @GetMapping("/task/{taskId}/getAll")
    public ResponseEntity<PageResponse<ProgressReportCreateResponseDTO>> getProgressReportByTaskId(
            @PathVariable String taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new PageResponse<>(progressReportService.getProgressReportByTaskId(taskId, PageRequest.of(page, size))));
    }
}
