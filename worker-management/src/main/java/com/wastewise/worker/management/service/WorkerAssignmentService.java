package com.wastewise.worker.management.service;

import com.wastewise.worker.management.dto.WorkerAssignmentDTO;

public interface WorkerAssignmentService {

    String assignWorkerToAssignment(String assignmentId, String workerId, WorkerAssignmentDTO dto);

    String updateSingleWorkerAssignment(String assignmentId, String oldWorkerId, String newWorkerId);

    String updateBothWorkerAssignments(String assignmentId,
                                       String oldWorkerId1, String oldWorkerId2,
                                       String newWorkerId1, String newWorkerId2);

    String deleteWorkerAssignment(String assignmentId);

}
