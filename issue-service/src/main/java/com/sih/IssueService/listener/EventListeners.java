package com.sih.IssueService.listener;

import com.sih.IssueService.service.IssueService;
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
    private final IssueService issueService;
    @Bean
    public Consumer<Map<String, String>> processIssueAcknowledgement() {
        return payload -> {
            log.info("processIssueAcknowledgement listener called with payload: {}", payload);
            issueService.ackIssue(payload.get("issueId"), payload.get("deptId"));
        };
    }
}
