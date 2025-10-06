package com.sih.IssueService.controller;

import com.sanskar.sih.issue.ResolvementRequestDTO;
import com.sanskar.sih.issue.ResolvementResponseDTO;
import com.sih.IssueService.service.ResolvementReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/issue")
public class ResolvementReportController {

    @Autowired
    private ResolvementReportService resolvementReportService;

    @PutMapping("/resolvement")
    public ResponseEntity<ResolvementResponseDTO> resolveIssue(
            @RequestBody ResolvementRequestDTO requestDTO,
            @RequestHeader("x-user-id") String userId
    ) {
        return ResponseEntity.ok(resolvementReportService.resolveIssue(requestDTO, userId));
    }
}
