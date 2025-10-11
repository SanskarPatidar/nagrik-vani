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
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor // for final or @NonNull fields
public class DepartmentAdminService {
    private final DepartmentAdminProfileRepository departmentAdminProfileRepository;
    private final IssueClient issueClient;
    private final StreamBridge streamBridge;

    public void acknowledgeIssue(String issueId, String userId) {
        log.info("Acknowledging issue with issueId: {} by userId: {}", issueId, userId);
        var profile = departmentAdminProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Department admin not found"));
//        FeignCallDelegation.execute(
//                () -> issueClient.acknowledgeIssue(issueId, profile.getId())
//        );
        Map<String, String> payload = new HashMap<>();
        payload.put("issueId", issueId);
        payload.put("deptId", profile.getId());
        streamBridge.send("issue-acknowledge-by-dept-out-0", payload);
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
