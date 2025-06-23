package com.wastewise.assignmentservice.service;

import com.wastewise.assignmentservice.dto.AssignmentDTO;

import java.util.List;

public interface AssignmentService {
    AssignmentDTO createAssignment(AssignmentDTO dto);
    AssignmentDTO getAssignmentById(String id);
    List<AssignmentDTO> getAllAssignments();
    void deleteAssignment(String id);
    AssignmentDTO updateAssignment(String id, AssignmentDTO dto);
}
