package com.sih.IssueService.client;

import com.sanskar.sih.departmentadmin.DepartmentAdminProfileResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "department-admin-service", path = "/department-admin")
public interface DepartmentAdminClient {

    @GetMapping("/profile/me")
    ResponseEntity<DepartmentAdminProfileResponseDTO> getDepartmentAdminById(@RequestHeader("x-user-id") String userId);
}
