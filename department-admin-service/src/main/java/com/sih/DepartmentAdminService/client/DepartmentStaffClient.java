package com.sih.DepartmentAdminService.client;

import com.sanskar.sih.departmentstaff.DepartmentStaffProfileInterchangeDTO;
import com.sanskar.sih.departmentstaff.DepartmentStaffProfileResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "department-staff-service", path = "/department-staff")
public interface DepartmentStaffClient {

    @GetMapping("/get/{departmentStaffId}")
    ResponseEntity<DepartmentStaffProfileResponseDTO> getStaffProfileById(
            @PathVariable String departmentStaffId
    );

    @GetMapping("/profile/me") // internal + external
    public ResponseEntity<DepartmentStaffProfileResponseDTO> getMyProfile(
            @RequestHeader("x-user-id") String userId
    );

//    @PutMapping("/update-profile")
//    ResponseEntity<Void> updateStaffProfile(
//            @RequestBody DepartmentStaffProfileResponseDTO staffProfile
//    );
}
