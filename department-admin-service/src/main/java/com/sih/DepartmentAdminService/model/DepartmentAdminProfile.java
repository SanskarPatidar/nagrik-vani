package com.sih.DepartmentAdminService.model;

import com.sanskar.sih.departmentadmin.DepartmentAdminProfileView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "department_admin_profiles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentAdminProfile implements DepartmentAdminProfileView {
    @Id
    private String id;
    private String userId; // reference to User
    private String departmentName;
    private String city;
    private String state;
    private String country;

    private String bio;
    private String profileImageUrl;

    private Long issuesAssigned;
    private Long issuesResolved;
    private Double averageResolutionTime; // in hours
    private Double performanceRating;
    private LocalDateTime tenureStartDate;
}