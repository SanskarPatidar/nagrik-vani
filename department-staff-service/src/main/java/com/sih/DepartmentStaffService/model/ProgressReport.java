package com.sih.DepartmentStaffService.model;

import com.sanskar.sih.departmentstaff.ProgressReportView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "progress_reports")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressReport implements ProgressReportView {
    @Id
    private String id;
    private String taskId; // reference to Task
    private String reportedById; // reference to DepartmentStaffProfile
    private LocalDateTime reportedAt;

    private String reportTitle;
    private String reportDescription;
    private List<String> imageUrls;
}
