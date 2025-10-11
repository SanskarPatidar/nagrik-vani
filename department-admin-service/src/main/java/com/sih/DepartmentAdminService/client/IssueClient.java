package com.sih.DepartmentAdminService.client;

import com.sanskar.sih.issue.IssueInterchangeDTO;
import com.sanskar.sih.issue.IssueSearchResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "issue-service", path = "/issue")
public interface IssueClient {

    @GetMapping("/get/{issueId}")
    ResponseEntity<IssueSearchResponseDTO> getIssueById(@PathVariable String issueId);

//    @PutMapping("/ack/{issueId}/{deptId}")
//    ResponseEntity<Void> acknowledgeIssue(
//            @PathVariable String issueId,
//            @PathVariable String deptId
//    );
}
