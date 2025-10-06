package com.sih.ComplaintService.controller;

import com.netflix.discovery.converters.Auto;
import com.sih.ComplaintService.dto.FeedbackRequestDTO;
import com.sih.ComplaintService.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/complaint/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PutMapping("/upsert")
    public ResponseEntity<Void> upsertFeedback(
            @RequestBody FeedbackRequestDTO request,
            @RequestHeader("x-user-id") String userId
    ) {
        feedbackService.upsertFeedback(request, userId);
        return ResponseEntity.ok().build();
    }
}
