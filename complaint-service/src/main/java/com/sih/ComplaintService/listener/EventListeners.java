package com.sih.ComplaintService.listener;

import com.sih.ComplaintService.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Consumer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class EventListeners {
    private final ComplaintService complaintService;
    @Bean
    public Consumer<Map<String, String>> processIssueAcknowledgement() {
        // The bean name is 'processIssueAcknowledgement'
        return payload -> {
            String issueId = payload.get("issueId");
            log.info("processIssueAcknowledgement listener called with payload: {}", payload);
            complaintService.acknowledgeComplaints(issueId);
        };
    }
}
