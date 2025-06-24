package com.wastewise.worker.management.service;

import com.wastewise.worker.management.dto.WorkerAssignmentDTO;

import java.util.List;

public interface WorkerAssignmentService {

    String assignWorkerToAssignment(String assignmentId, String workerId, WorkerAssignmentDTO dto);

    WorkerAssignmentDTO findWorkerAssignment(String workerId);

    List<WorkerAssignmentDTO> findAllWorkerAssignments();

    String updateSingleWorkerAssignment(String assignmentId, String oldWorkerId, String newWorkerId);

    String updateBothWorkerAssignments(String assignmentId,
                                       String oldWorkerId1, String oldWorkerId2,
                                       String newWorkerId1, String newWorkerId2);

    String deleteWorkerAssignment(String assignmentId);

}
