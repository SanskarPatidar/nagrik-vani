package com.sih.IssueService.controller;

import com.sanskar.sih.complaint.ComplaintRequestDTO;
import com.sanskar.sih.issue.*;
import com.sih.IssueService.dto.*;
import com.sih.IssueService.model.Issue;
import com.sih.IssueService.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/issue")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @GetMapping("/getAll")
    public ResponseEntity<PageResponse<IssueSearchResponseDTO>> getAllIssues(
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new PageResponse<>(issueService.getAllIssues(PageRequest.of(page, size))));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<IssueSearchResponseDTO>> searchIssues(
            @RequestBody IssueSearchRequestDTO requestDTO,
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new PageResponse<>(issueService.searchIssues(requestDTO, PageRequest.of(page, size))));
    }

    @PutMapping("/like/{issueId}")
    public ResponseEntity<Void> likeIssue(
            @PathVariable String issueId
    ) {
        issueService.likeIssue(issueId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get/{issueId}") // internal + external use
    public ResponseEntity<IssueSearchResponseDTO> getIssueById(@PathVariable String issueId) {
        return ResponseEntity.ok(issueService.getIssueById(issueId));
    }

    @GetMapping("/findByTypeAndRadius") // internal use
    public ResponseEntity<IssueInterchangeDTO> findByTypeAndWithinRadius(
            @RequestParam String type,
            @RequestParam double centerLat,
            @RequestParam double centerLon,
            @RequestParam double radiusInMeters
    ) {
        return ResponseEntity.ok(issueService.findByTypeAndWithinRadius(type, centerLat, centerLon, radiusInMeters));
    }

    @PutMapping("/create") // internal use
    public ResponseEntity<IssueInterchangeDTO> createIssue(@RequestBody ComplaintRequestDTO request) {
        return ResponseEntity.ok(issueService.createIssue(request));
    }

    @PutMapping("/ack/{issueId}/{deptId}") // internal use
    public ResponseEntity<Void> acknowledgeIssue(
            @PathVariable String issueId,
            @PathVariable String deptId
    ) {
        issueService.ackIssue(issueId, deptId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get/resolvementReportId/{resId}") // internal use
    public ResponseEntity<IssueInterchangeDTO> getIssueByResolvementReportId(@PathVariable String resId) {
        return ResponseEntity.ok(issueService.getIssueByResolvementReportId(resId));
    }


}
