package com.wastewise.worker.management.mapper;

import com.wastewise.worker.management.dto.WorkerCreateDTO;
import com.wastewise.worker.management.dto.WorkerDTO;
import com.wastewise.worker.management.dto.WorkerUpdateDTO;
import com.wastewise.worker.management.enums.WorkerStatus;
import com.wastewise.worker.management.model.Worker;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-20T11:14:50+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21 (Oracle Corporation)"
)
@Component
public class WorkerMapperImpl implements WorkerMapper {

    @Override
    public Worker toEntity(WorkerCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Worker worker = new Worker();

        worker.setName( dto.getName() );
        worker.setContactNumber( dto.getContactNumber() );
        worker.setContactEmail( dto.getContactEmail() );
        if ( dto.getWorkerStatus() != null ) {
            worker.setWorkerStatus( Enum.valueOf( WorkerStatus.class, dto.getWorkerStatus() ) );
        }
        worker.setRoleId( dto.getRoleId() );

        return worker;
    }

    @Override
    public WorkerDTO toDTO(Worker entity) {
        if ( entity == null ) {
            return null;
        }

        WorkerDTO workerDTO = new WorkerDTO();

        workerDTO.setWorkerId( entity.getWorkerId() );
        workerDTO.setName( entity.getName() );
        workerDTO.setContactNumber( entity.getContactNumber() );
        workerDTO.setContactEmail( entity.getContactEmail() );
        workerDTO.setWorkerStatus( entity.getWorkerStatus() );

        return workerDTO;
    }

    @Override
    public void updateWorkerFromDTO(WorkerUpdateDTO dto, Worker entity) {
        if ( dto == null ) {
            return;
        }

        entity.setName( dto.getName() );
        entity.setContactNumber( dto.getContactNumber() );
        entity.setContactEmail( dto.getContactEmail() );
        if ( dto.getWorkerStatus() != null ) {
            entity.setWorkerStatus( Enum.valueOf( WorkerStatus.class, dto.getWorkerStatus() ) );
        }
        else {
            entity.setWorkerStatus( null );
        }
        entity.setRoleId( dto.getRoleId() );
    }
}
