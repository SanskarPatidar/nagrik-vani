package com.sih.DepartmentStaffService.service;

import com.sanskar.common.exception.FeignCallDelegation;
import com.sanskar.common.exception.ForbiddenAccessException;
import com.sanskar.common.exception.ResourceConflictException;
import com.sanskar.sih.departmentadmin.DepartmentAdminProfileResponseDTO;
import com.sanskar.sih.departmentadmin.TaskCreateResponseDTO;
import com.sanskar.sih.departmentadmin.TaskInterchangeDTO;
import com.sanskar.sih.departmentstaff.DepartmentStaffProfileResponseDTO;
import com.sanskar.sih.departmentstaff.ProgressReportCreateRequestDTO;
import com.sanskar.sih.departmentstaff.ProgressReportCreateResponseDTO;
import com.sanskar.common.exception.NotFoundException;
import com.sih.DepartmentStaffService.client.DepartmentAdminClient;
import com.sih.DepartmentStaffService.model.DepartmentStaffProfile;
import com.sih.DepartmentStaffService.model.ProgressReport;
import com.sih.DepartmentStaffService.repository.DepartmentStaffProfileRepository;
import com.sih.DepartmentStaffService.repository.ProgressReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor // for final or @NonNull fields
public class DepartmentStaffService {
    private final DepartmentStaffProfileRepository departmentStaffProfileRepository;
    private final DepartmentAdminClient departmentAdminClient;

    public DepartmentStaffProfileResponseDTO getDepartmentStaffById(String departmentStaffId) {
        log.info("Getting department staff profile for dept staff id: {}", departmentStaffId);
        return departmentStaffProfileRepository.findById(departmentStaffId)
                .map(DepartmentStaffProfileResponseDTO::new)
                .orElseThrow(() -> new NotFoundException("Department staff profile not found for id: " + departmentStaffId));
    }

    public Page<DepartmentStaffProfileResponseDTO> getDepartmentStaffByDepartmentId(String userId, Pageable pageable) {
        log.info("Getting department staffs for department admin user id: {}", userId);
        DepartmentAdminProfileResponseDTO adminProfile = FeignCallDelegation.execute(
                () -> departmentAdminClient.getDepartmentAdminById(userId)
        );

        return departmentStaffProfileRepository.findAllByDepartmentId(adminProfile.getId(), pageable)
                .map(DepartmentStaffProfileResponseDTO::new);
    }

    public void updateStaffProfile(DepartmentStaffProfileResponseDTO staffProfile) {
        DepartmentStaffProfile existingProfile = departmentStaffProfileRepository.findById(staffProfile.getId())
                .orElseThrow(() -> new NotFoundException("Staff profile not found for id: " + staffProfile.getId()));

        existingProfile.setId(staffProfile.getId());
        existingProfile.setDepartmentId(staffProfile.getDepartmentId());
        existingProfile.setFullName(staffProfile.getFullName());
        existingProfile.setProfileImageUrl(staffProfile.getProfileImageUrl());
        existingProfile.setTasksCompleted(staffProfile.getTasksCompleted());
        existingProfile.setTasksFailed(staffProfile.getTasksFailed());
        existingProfile.setWorkRating(staffProfile.getWorkRating());

        departmentStaffProfileRepository.save(existingProfile);
    }

    public DepartmentStaffProfileResponseDTO getMyProfile(String userId) {
        log.info("Getting department staff profile for UserId: {}", userId);
        return departmentStaffProfileRepository.findByUserId(userId)
                .map(DepartmentStaffProfileResponseDTO::new)
                .orElseThrow(() -> new NotFoundException("Department staff profile not found for user id: " + userId));
    }

    public DepartmentStaffProfileResponseDTO addDepartmentStaff(String staffId, String userId) {
        log.info("Adding department staff for dept admin user id: {}", userId);
        DepartmentAdminProfileResponseDTO adminProfile = FeignCallDelegation.execute(
                () -> departmentAdminClient.getDepartmentAdminById(userId)
        );

        DepartmentStaffProfile staffProfile = departmentStaffProfileRepository.findById(staffId)
                .orElseThrow(() -> new NotFoundException("Staff profile not found for id: " + staffId));

        if(staffProfile.getDepartmentId() != null)
            throw new ResourceConflictException("Staff already assigned to a department");

        staffProfile.setDepartmentId(adminProfile.getId());

        return new DepartmentStaffProfileResponseDTO(departmentStaffProfileRepository.save(staffProfile));
    }

    public DepartmentStaffProfileResponseDTO removeDepartmentStaff(String staffId, String userId) {
        DepartmentAdminProfileResponseDTO adminProfile = FeignCallDelegation.execute(
                () -> departmentAdminClient.getDepartmentAdminById(userId)
        );

        DepartmentStaffProfile staffProfile = departmentStaffProfileRepository.findById(staffId)
                .orElseThrow(() -> new NotFoundException("Staff profile not found for Id: " + staffId));

        if(!staffProfile.getDepartmentId().equals(adminProfile.getId()))
            throw new ForbiddenAccessException("Staff not assigned to your department");

        staffProfile.setDepartmentId(null);

        return new DepartmentStaffProfileResponseDTO(departmentStaffProfileRepository.save(staffProfile));
    }
}
