package com.sih.DepartmentAdminService.controller;

import com.sanskar.sih.departmentadmin.DepartmentAdminProfileInterchangeDTO;
import com.sanskar.sih.departmentadmin.DepartmentAdminProfileResponseDTO;
import com.sih.DepartmentAdminService.dto.PageResponse;
import com.sih.DepartmentAdminService.service.DepartmentAdminService;
import com.sih.DepartmentAdminService.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/department-admin")
public class DepartmentAdminController {

    @Autowired
    private DepartmentAdminService departmentAdminService;

    @GetMapping("/profile/get/{departmentId}")
    public ResponseEntity<DepartmentAdminProfileResponseDTO> getDepartmentAdminProfileByDepartmentId(
            @PathVariable String departmentId
    ) {
        return ResponseEntity.ok(departmentAdminService.getDepartmentAdminProfileByDepartmentId(departmentId));
    }

    @PostMapping("/ack/{issueId}")
    public ResponseEntity<Void> acknowledgeIssue(
            @PathVariable String issueId,
            @RequestHeader("x-user-id") String userId
    ) {
        departmentAdminService.acknowledgeIssue(issueId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAll") // for all users
    public ResponseEntity<PageResponse<DepartmentAdminProfileResponseDTO>> getAllDepartmentAdmins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new PageResponse<>(departmentAdminService.getAllDepartmentAdmins(PageRequest.of(page,size))));
    }

    @GetMapping("/profile/me") // internal + external use
    public ResponseEntity<DepartmentAdminProfileResponseDTO> getMyProfile(
            @RequestHeader("x-user-id") String userId
    ) {
        return ResponseEntity.ok(departmentAdminService.getDepartmentAdminProfileByUserId(userId));
    }

}
