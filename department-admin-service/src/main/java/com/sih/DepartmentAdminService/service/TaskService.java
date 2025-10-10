package com.sih.DepartmentAdminService.service;

import com.sanskar.common.exception.FeignCallDelegation;
import com.sanskar.common.exception.ForbiddenAccessException;
import com.sanskar.common.exception.InvalidInputException;
import com.sanskar.sih.departmentadmin.TaskCreateRequestDTO;
import com.sanskar.sih.departmentadmin.TaskCreateResponseDTO;
import com.sanskar.sih.departmentadmin.TaskStatus;
import com.sanskar.sih.departmentstaff.DepartmentStaffProfileInterchangeDTO;
import com.sanskar.sih.departmentstaff.DepartmentStaffProfileResponseDTO;
import com.sanskar.common.exception.NotFoundException;
import com.sanskar.sih.issue.IssueSearchResponseDTO;
import com.sih.DepartmentAdminService.client.DepartmentStaffClient;
import com.sih.DepartmentAdminService.client.IssueClient;
import com.sih.DepartmentAdminService.model.DepartmentAdminProfile;
import com.sih.DepartmentAdminService.model.Task;
import com.sih.DepartmentAdminService.repository.DepartmentAdminProfileRepository;
import com.sih.DepartmentAdminService.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor // for final or @NonNull fields
public class TaskService {
    private final TaskRepository taskRepository;
    private final DepartmentAdminProfileRepository departmentAdminProfileRepository;
    private final DepartmentStaffClient departmentStaffClient;
    private final IssueClient issueClient;

    public TaskCreateResponseDTO createTask(TaskCreateRequestDTO request, String userId) {

        log.info("Create task request from userId: {}", userId);

        DepartmentAdminProfile adminProfile = departmentAdminProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Admin profile not found"));

        log.info("Admin profile: {}", adminProfile);

        var staffResponse = FeignCallDelegation.execute(
                () -> departmentStaffClient.getStaffProfileById(request.getAssignedToId())
        );

        log.info("Staff profile(even though this is called, it does not mean it can escape runtime error): {}", staffResponse);

        if(staffResponse == null || !staffResponse.getDepartmentId().equals(adminProfile.getId()))
            throw new NotFoundException("Staff profile not found");

        var issue = FeignCallDelegation.execute(() -> issueClient.getIssueById(request.getAssignedIssueId()));

        log.info("Issue(even though this is called, it does not mean it can escape runtime error): {}", issue);

        if(issue == null || !issue.getAssignedToId().equals(adminProfile.getId()))
            throw new NotFoundException("Issue Not Found");

        Task task = Task.builder()
                .id(UUID.randomUUID().toString())
                .assignedFromId(userId)
                .assignedToId(request.getAssignedToId())
                .assignedIssueId(request.getAssignedIssueId())
                .issuedAt(LocalDateTime.now())
                .title(request.getTitle())
                .description(request.getDescription())
                .status(TaskStatus.PENDING)
                .imageUrls(request.getImageUrls())
                .build();

        return new TaskCreateResponseDTO(taskRepository.save(task));
    }

    public TaskCreateResponseDTO setTaskStatus(String taskId, TaskStatus status, String userId) {

        log.info("Set task status request from userId: {}", userId);

        DepartmentAdminProfile adminProfile = departmentAdminProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Admin profile not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        if(!task.getAssignedFromId().equals(adminProfile.getId()))
            throw new ForbiddenAccessException("Unauthorized");

        var staffProfile = FeignCallDelegation.execute(
                () -> departmentStaffClient.getStaffProfileById(task.getAssignedToId())
        );
        if(staffProfile == null || !staffProfile.getDepartmentId().equals(adminProfile.getId()))
            throw new NotFoundException("Unknown staff profile not found");

        switch (status) {
            case COMPLETED -> {
                task.setStatus(TaskStatus.COMPLETED);
                staffProfile.setTasksCompleted(staffProfile.getTasksCompleted() + 1);
            }
            case FAILED -> {
                task.setStatus(TaskStatus.FAILED);
                staffProfile.setTasksFailed(staffProfile.getTasksFailed() + 1);
            }
            case CLOSED -> task.setStatus(TaskStatus.CLOSED);
            default -> throw new InvalidInputException("Invalid status");
        }
        Long tasksCompleted = staffProfile.getTasksCompleted();
        Long tasksFailed = staffProfile.getTasksFailed();

        Double score = (double) tasksCompleted / (tasksCompleted + tasksFailed + 30) * 10.0; // Saturation factor of 30 to prevent drastic changes initially
        staffProfile.setWorkRating(score);
        log.info("Updated staff profile: {}", staffProfile);
        FeignCallDelegation.execute(() -> departmentStaffClient.updateStaffProfile(staffProfile));
        task.setCompletedAt(LocalDateTime.now().toString());
        return new TaskCreateResponseDTO(taskRepository.save(task));
    }

    public TaskCreateResponseDTO getTaskById(String taskId) {
        log.info("Get task by id: {}", taskId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        return new TaskCreateResponseDTO(task);
    }

    public Page<TaskCreateResponseDTO> getAllTasks(String userId, Pageable pageable) {
        log.info("Get all tasks request from userId: {}", userId);
        DepartmentStaffProfileResponseDTO staffProfile = FeignCallDelegation.execute(() -> departmentStaffClient.getMyProfile(userId));

        return taskRepository.findAllByAssignedToId(staffProfile.getId(), pageable)
                .map(TaskCreateResponseDTO::new);
    }

    public Page<TaskCreateResponseDTO> getAllTasksByStatus(String userId, TaskStatus status, Pageable pageable) {
        log.info("Get All Tasks by status {}, request from userId: {}", status, userId);
        DepartmentStaffProfileResponseDTO staffProfile = FeignCallDelegation.execute(() -> departmentStaffClient.getMyProfile(userId));

        return taskRepository.findAllByAssignedToIdAndStatus(staffProfile.getId(), status, pageable)
                .map(TaskCreateResponseDTO::new);
    }

}
