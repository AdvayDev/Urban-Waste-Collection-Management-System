package com.wastewise.workermanagement.mapper;

import com.wastewise.workermanagement.dto.WorkerCreateDTO;
import com.wastewise.workermanagement.dto.WorkerDTO;
import com.wastewise.workermanagement.dto.WorkerUpdateDTO;
import com.wastewise.workermanagement.model.Worker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WorkerMapper {

    Worker toEntity(WorkerCreateDTO dto);

    @Mapping(source = "workerId", target = "workerId")
    WorkerDTO toDTO(Worker entity);

    @Mapping(target = "workerId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    void updateWorkerFromDTO(WorkerUpdateDTO dto, @MappingTarget Worker entity);
}