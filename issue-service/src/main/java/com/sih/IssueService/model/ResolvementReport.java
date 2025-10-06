package com.sih.IssueService.model;


import com.sanskar.sih.issue.ResolvementReportView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("resolvement_reports")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResolvementReport implements ResolvementReportView {
    private String id;
    private String issueId; // reference to Issue
    private String resolvedById; // reference to DepartmentProfile
    private LocalDateTime resolvedAt;

    private String title;
    private String description;
    private List<String> imageUrls;
}
