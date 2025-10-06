package com.sih.DepartmentAdminService.repository;

import com.sih.DepartmentAdminService.model.DepartmentAdminProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentAdminProfileRepository extends MongoRepository<DepartmentAdminProfile, String> {
    Optional<DepartmentAdminProfile> findByUserId(String userId);
    Page<DepartmentAdminProfile> findAll(Pageable pageable);
}
