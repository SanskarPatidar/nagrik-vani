package com.sih.ComplaintService.controller;

import com.sanskar.sih.complaint.ComplaintRequestDTO;
import com.sanskar.sih.complaint.ComplaintResponseDTO;
import com.sih.ComplaintService.dto.PageResponse;
import com.sih.ComplaintService.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/complaint")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @PostMapping("/post")
    public ResponseEntity<ComplaintResponseDTO> postComplaint(
            @RequestHeader("x-user-id") String userId,
            @RequestBody ComplaintRequestDTO complaintRequestDTO
    ) {
        return ResponseEntity.ok(complaintService.postComplaint(complaintRequestDTO, userId));
    }

    @GetMapping("/mine")
    public ResponseEntity<PageResponse<ComplaintResponseDTO>> getMyComplaints(
            @RequestHeader("x-user-id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new PageResponse<>(complaintService.getMyComplaints(PageRequest.of(page, size), userId)));
    }

    @GetMapping("/all/issue/{issueId}")
    public ResponseEntity<PageResponse<ComplaintResponseDTO>> getAllComplaintsByIssueId(
            @PathVariable String issueId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new PageResponse<>(complaintService.getAllComplaintsByIssueId(PageRequest.of(page, size), issueId)));
    }

    @PutMapping("/ack/issue/{issueId}") // internal use
    ResponseEntity<Void> acknowledgeComplaints(@PathVariable String issueId) {
        complaintService.acknowledgeComplaints(issueId);
        return ResponseEntity.ok().build();
    }

}
