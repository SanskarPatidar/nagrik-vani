package com.sih.DepartmentStaffService.controller;

import com.sanskar.sih.departmentstaff.DepartmentStaffProfileInterchangeDTO;
import com.sanskar.sih.departmentstaff.DepartmentStaffProfileResponseDTO;
import com.sanskar.sih.departmentstaff.ProgressReportCreateRequestDTO;
import com.sanskar.sih.departmentstaff.ProgressReportCreateResponseDTO;
import com.sih.DepartmentStaffService.dto.PageResponse;
import com.sih.DepartmentStaffService.service.DepartmentStaffService;
import com.sih.DepartmentStaffService.service.ProgressReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/department-staff")
public class DepartmentStaffController {

    @Autowired
    private DepartmentStaffService departmentStaffService;

    @GetMapping("/getAll") // multiple staff
    public ResponseEntity<PageResponse<DepartmentStaffProfileResponseDTO>> getDepartmentStaffByDepartmentId(
            @RequestHeader("x-user-id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new PageResponse<>(departmentStaffService.getDepartmentStaffByDepartmentId(userId, PageRequest.of(page, size))));
    }

    @GetMapping("/get/{departmentStaffId}") // internal + external
    public ResponseEntity<DepartmentStaffProfileResponseDTO> getDepartmentStaffById(@PathVariable String departmentStaffId) {
        return ResponseEntity.ok(departmentStaffService.getDepartmentStaffById(departmentStaffId));
    }

    @GetMapping("/profile/me") // internal + external
    public ResponseEntity<DepartmentStaffProfileResponseDTO> getMyProfile(@RequestHeader("x-user-id") String userId) {
        return ResponseEntity.ok(departmentStaffService.getMyProfile(userId));
    }

    @PutMapping("/update-profile") // internal use
    public ResponseEntity<Void> updateStaffProfile(@RequestBody DepartmentStaffProfileResponseDTO staffProfile) {
        departmentStaffService.updateStaffProfile(staffProfile);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/add-staff/{staffId}") // for super admin utility
    public ResponseEntity<DepartmentStaffProfileResponseDTO> addDepartmentStaff(
            @PathVariable String staffId,
            @RequestHeader("x-user-id") String userId
    ) {
        return ResponseEntity.ok(departmentStaffService.addDepartmentStaff(staffId, userId));
    }

    @PutMapping("/remove-staff/{staffId}") // for super admin utility
    public ResponseEntity<DepartmentStaffProfileResponseDTO> removeDepartmentStaff(
            @PathVariable String staffId,
            @RequestHeader("x-user-id") String userId
    ) {
        return ResponseEntity.ok(departmentStaffService.removeDepartmentStaff(staffId, userId));
    }

}
