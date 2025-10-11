package com.sih.DepartmentStaffService.listener;

import com.sanskar.sih.departmentstaff.DepartmentStaffProfileResponseDTO;
import com.sih.DepartmentStaffService.service.DepartmentStaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class EventListeners {
    private final DepartmentStaffService departmentStaffService;
    @Bean
    public Consumer<DepartmentStaffProfileResponseDTO> processDepartmentStaffProfileUpdateEvent() {
        return profile -> {
            log.info("processDepartmentStaffProfileUpdateEvent listener called with payload: {}", profile);
            departmentStaffService.updateStaffProfile(profile);
        };
    }
}
