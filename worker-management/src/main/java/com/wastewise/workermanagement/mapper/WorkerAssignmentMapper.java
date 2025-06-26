package com.wastewise.workermanagement.mapper;

import com.wastewise.workermanagement.dto.WorkerAssignmentDTO;
import com.wastewise.workermanagement.model.WorkerAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkerAssignmentMapper {

    @Mapping(source = "id.assignmentId", target = "assignmentId")
    @Mapping(source = "id.workerId", target = "workerId")
    WorkerAssignmentDTO toDTO(WorkerAssignment entity);
}