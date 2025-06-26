package com.wastewise.workermanagement.repository;

import com.wastewise.workermanagement.model.WorkerAssignment;
import com.wastewise.workermanagement.model.WorkerAssignmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerAssignmentRepository extends JpaRepository<WorkerAssignment, WorkerAssignmentId> {
    List<WorkerAssignment> findByIdAssignmentId(String id);

    WorkerAssignment findByIdWorkerId(String id);
}
