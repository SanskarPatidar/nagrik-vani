package com.sih.DepartmentStaffService.repository;

import com.sih.DepartmentStaffService.model.DepartmentStaffProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface DepartmentStaffProfileRepository extends MongoRepository<DepartmentStaffProfile, String> {
    Page<DepartmentStaffProfile> findAllByDepartmentId(String id, Pageable pageable);

    Optional<DepartmentStaffProfile> findByUserId(String userId);
}
