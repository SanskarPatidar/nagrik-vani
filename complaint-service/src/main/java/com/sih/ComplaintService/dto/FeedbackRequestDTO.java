package com.sih.ComplaintService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequestDTO {
    private String complaintId;
    private String resolvementReportId;

    private boolean isSatisfied;
    private String description;
}
