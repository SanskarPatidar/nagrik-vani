package com.sih.DepartmentStaffService.model;


import com.sanskar.sih.departmentadmin.DepartmentAdminProfileView;
import com.sanskar.sih.departmentstaff.DepartmentStaffProfileView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "department_staff_profiles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentStaffProfile implements DepartmentStaffProfileView {
    @Id
    private String id;
    private String userId; // reference to User
    private String departmentId; // reference to department
    private String fullName;

    private String profileImageUrl;
    private Long tasksCompleted;
    private Long tasksFailed;
    private Double workRating;
}
