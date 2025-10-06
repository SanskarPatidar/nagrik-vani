package com.sih.DepartmentAdminService.repository;

import com.sanskar.sih.departmentadmin.TaskStatus;
import com.sih.DepartmentAdminService.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task,String> {
    Page<Task> findAllByAssignedToId(String assignedToId, Pageable pageable);
    Page<Task> findAllByAssignedToIdAndStatus(String id, TaskStatus taskStatus, Pageable pageable);
}
