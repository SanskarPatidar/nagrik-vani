package com.sih.DepartmentAdminService.model;


import com.sanskar.sih.departmentadmin.TaskStatus;
import com.sanskar.sih.departmentadmin.TaskView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "tasks")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task implements TaskView {
    @Id
    private String id;
    private String assignedFromId; // reference to DepartmentAdminProfile
    private String assignedToId; // reference to DepartmentStaffProfile
    private String assignedIssueId; // reference to Issue
    private LocalDateTime issuedAt;
    private String completedAt;

    private String title;
    private String description;
    private TaskStatus status;
    private List<String> imageUrls;
}
