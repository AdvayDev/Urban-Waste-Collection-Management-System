package com.wastewise.worker.management.repository;

import com.wastewise.worker.management.dto.WorkerAssignmentDTO;
import com.wastewise.worker.management.model.WorkerAssignment;
import com.wastewise.worker.management.model.WorkerAssignmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerAssignmentRepository extends JpaRepository<WorkerAssignment, WorkerAssignmentId> {
    List<WorkerAssignment> findByIdAssignmentId(String id);

    WorkerAssignment findByIdWorkerId(String id);
}
