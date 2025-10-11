package com.sih.IssueService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "complaint-service", path = "/complaint")
public interface ComplaintClient {

//    @PutMapping("/ack/issue/{issueId}")
//    ResponseEntity<Void> acknowledgeComplaints(@PathVariable String issueId);
}
