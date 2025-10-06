package com.sih.DepartmentAdminService.controller;

import com.sanskar.sih.departmentadmin.TaskCreateRequestDTO;
import com.sanskar.sih.departmentadmin.TaskCreateResponseDTO;
import com.sanskar.sih.departmentadmin.TaskStatus;
import com.sih.DepartmentAdminService.dto.PageResponse;
import com.sih.DepartmentAdminService.model.Task;
import com.sih.DepartmentAdminService.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/department-admin/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/create")
    public ResponseEntity<TaskCreateResponseDTO> createTask(
            @RequestHeader("x-user-id") String userId,
            @RequestBody TaskCreateRequestDTO request
    ) {
        return ResponseEntity.ok(taskService.createTask(request, userId));
    }

    @PutMapping("/{taskId}/status/{status}")
    public ResponseEntity<TaskCreateResponseDTO> setTaskStatus(
            @PathVariable String taskId,
            @PathVariable TaskStatus status,
            @RequestHeader("x-user-id") String userId
    ) {
        return ResponseEntity.ok(taskService.setTaskStatus(taskId, status, userId));
    }

    @GetMapping("/getAll") // for department staff
    public ResponseEntity<PageResponse<TaskCreateResponseDTO>> getAllTasks(
            @RequestHeader("x-user-id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new PageResponse<>(taskService.getAllTasks(userId, PageRequest.of(page, size))));
    }

    @GetMapping("/getAll/{status}")
    public ResponseEntity<PageResponse<TaskCreateResponseDTO>> getAllPendingTasks(
            @RequestHeader("x-user-id") String userId,
            @PathVariable TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new PageResponse<>(taskService.getAllTasksByStatus(userId, status, PageRequest.of(page, size))));
    }

    @GetMapping("/{taskId}") // internal + external
    public ResponseEntity<TaskCreateResponseDTO> getTaskById(
            @PathVariable String taskId
    ) {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }
}
