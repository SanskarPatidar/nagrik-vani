package com.sih.DepartmentAdminService.service;

import com.sanskar.sih.departmentadmin.DepartmentAdminProfileInterchangeDTO;
import com.sanskar.sih.departmentadmin.DepartmentAdminProfileResponseDTO;
import com.sih.DepartmentAdminService.client.IssueClient;
import com.sih.DepartmentAdminService.model.DepartmentAdminProfile;
import com.sih.DepartmentAdminService.repository.DepartmentAdminProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DepartmentAdminService {

    @Autowired
    private DepartmentAdminProfileRepository departmentAdminProfileRepository;

    @Autowired
    private IssueClient issueClient;

    public void acknowledgeIssue(String issueId, String userId) {
        log.info("Acknowledging issue with issueId: {} by userId: {}", issueId, userId);
        var profile = departmentAdminProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Department Admin not found"));
        issueClient.acknowledgeIssue(issueId, profile.getId());
    }

    public Page<DepartmentAdminProfileResponseDTO> getAllDepartmentAdmins(Pageable pageable) {
        log.info("Fetching all Department Admin profiles with pagination");
        return departmentAdminProfileRepository.findAll(pageable)
                .map(DepartmentAdminProfileResponseDTO::new);
    }

    public DepartmentAdminProfileResponseDTO getDepartmentAdminProfileByDepartmentId(String departmentId) {
        log.info("Fetching Department Admin for departmentId: " + departmentId);
        DepartmentAdminProfile profile = departmentAdminProfileRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department Admin not found"));

        return new DepartmentAdminProfileResponseDTO(profile);
    }

    public DepartmentAdminProfileResponseDTO getDepartmentAdminProfileByUserId(String userId) {
        log.info("Fetching Department Admin profile for userId: {}", userId);
        var profile = departmentAdminProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Department Admin not found"));

        return new DepartmentAdminProfileResponseDTO(profile);
    }
}
