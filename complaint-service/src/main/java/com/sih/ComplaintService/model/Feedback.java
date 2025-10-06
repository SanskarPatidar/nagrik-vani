package com.sih.ComplaintService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "feedbacks")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {
    @Id
    private String id;
    private String complaintId; // reference to Complaint
    private String resolvementReportId; // reference to ResolvementReport
    private LocalDateTime submittedAt;

    private boolean isSatisfied;
    private String description;
}
