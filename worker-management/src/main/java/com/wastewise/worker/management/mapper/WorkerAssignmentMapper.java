package com.wastewise.worker.management.mapper;

import com.wastewise.worker.management.dto.WorkerAssignmentDTO;
import com.wastewise.worker.management.model.WorkerAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkerAssignmentMapper {

    @Mapping(source = "id.assignmentId", target = "assignmentId")
    @Mapping(source = "id.workerId", target = "workerId")
    WorkerAssignmentDTO toDTO(WorkerAssignment entity);
}