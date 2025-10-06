package com.sih.DepartmentStaffService.repository;

import com.sih.DepartmentStaffService.model.ProgressReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgressReportRepository extends MongoRepository<ProgressReport, String> {
    Page<ProgressReport> findAllByTaskId(String taskId, Pageable pageable);
}
