package com.sih.ComplaintService.client;

import com.sanskar.sih.complaint.ComplaintRequestDTO;
import com.sanskar.sih.issue.IssueInterchangeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "issue-service", path = "/issue")
public interface IssueClient {

    @GetMapping("/findByTypeAndRadius")
    public ResponseEntity<IssueInterchangeDTO> findByTypeAndWithinRadius(
            @RequestParam("type") String type,
            @RequestParam("centerLat") double centerLat,
            @RequestParam("centerLon") double centerLon,
            @RequestParam("radiusInMeters") double radiusInMeters
    );

    @PutMapping("/create")
    ResponseEntity<IssueInterchangeDTO> createIssue(@RequestBody ComplaintRequestDTO request);

    @GetMapping("/get/resolvementReportId/{resId}") // internal use
    public ResponseEntity<IssueInterchangeDTO> getIssueByResolvementReportId(@PathVariable String resId);
}
