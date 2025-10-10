package com.sih.DepartmentAdminService.service;

import com.sanskar.common.exception.FeignCallDelegation;
import com.sanskar.sih.departmentadmin.DepartmentAdminProfileInterchangeDTO;
import com.sanskar.sih.departmentadmin.DepartmentAdminProfileResponseDTO;
import com.sanskar.common.exception.NotFoundException;
import com.sih.DepartmentAdminService.client.IssueClient;
import com.sih.DepartmentAdminService.model.DepartmentAdminProfile;
import com.sih.DepartmentAdminService.repository.DepartmentAdminProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor // for final or @NonNull fields
public class DepartmentAdminService {
    private final DepartmentAdminProfileRepository departmentAdminProfileRepository;
    private final IssueClient issueClient;

    public void acknowledgeIssue(String issueId, String userId) {
        log.info("Acknowledging issue with issueId: {} by userId: {}", issueId, userId);
        var profile = departmentAdminProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Department admin not found"));
        FeignCallDelegation.execute(
                () -> issueClient.acknowledgeIssue(issueId, profile.getId())
        );
    }

    public Page<DepartmentAdminProfileResponseDTO> getAllDepartmentAdmins(Pageable pageable) {
        log.info("Fetching all department admin profiles with pagination");
        return departmentAdminProfileRepository.findAll(pageable)
                .map(DepartmentAdminProfileResponseDTO::new);
    }

    public DepartmentAdminProfileResponseDTO getDepartmentAdminProfileByDepartmentId(String departmentId) {
        log.info("Fetching Department Admin for departmentId: {}", departmentId);
        DepartmentAdminProfile profile = departmentAdminProfileRepository.findById(departmentId)
                .orElseThrow(() -> new NotFoundException("Department admin not found"));

        return new DepartmentAdminProfileResponseDTO(profile);
    }

    public DepartmentAdminProfileResponseDTO getDepartmentAdminProfileByUserId(String userId) {
        log.info("Fetching department admin profile for userId: {}", userId);
        var profile = departmentAdminProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Department admin not found"));

        return new DepartmentAdminProfileResponseDTO(profile);
    }
}
