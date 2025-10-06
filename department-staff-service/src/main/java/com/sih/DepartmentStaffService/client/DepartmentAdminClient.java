package com.sih.DepartmentStaffService.client;

import com.sanskar.sih.departmentadmin.DepartmentAdminProfileInterchangeDTO;
import com.sanskar.sih.departmentadmin.DepartmentAdminProfileResponseDTO;
import com.sanskar.sih.departmentadmin.TaskCreateResponseDTO;
import com.sanskar.sih.departmentadmin.TaskInterchangeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "department-admin-service", path = "/department-admin")
public interface DepartmentAdminClient {

    @GetMapping("/profile/me")
    ResponseEntity<DepartmentAdminProfileResponseDTO> getDepartmentAdminById(@RequestHeader("x-user-id") String userId);

    @GetMapping("/task/{taskId}")
    ResponseEntity<TaskCreateResponseDTO> getTaskById(@PathVariable String taskId);
}
