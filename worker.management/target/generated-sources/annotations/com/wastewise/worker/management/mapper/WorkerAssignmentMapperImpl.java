package com.wastewise.worker.management.mapper;

import com.wastewise.worker.management.dto.WorkerAssignmentDTO;
import com.wastewise.worker.management.model.WorkerAssignment;
import com.wastewise.worker.management.model.WorkerAssignmentId;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-23T09:34:15+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21 (Oracle Corporation)"
)
@Component
public class WorkerAssignmentMapperImpl implements WorkerAssignmentMapper {

    @Override
    public WorkerAssignmentDTO toDTO(WorkerAssignment entity) {
        if ( entity == null ) {
            return null;
        }

        WorkerAssignmentDTO workerAssignmentDTO = new WorkerAssignmentDTO();

        workerAssignmentDTO.setAssignmentId( entityIdAssignmentId( entity ) );
        workerAssignmentDTO.setWorkerId( entityIdWorkerId( entity ) );
        workerAssignmentDTO.setZoneId( entity.getZoneId() );
        workerAssignmentDTO.setRouteId( entity.getRouteId() );
        if ( entity.getShift() != null ) {
            workerAssignmentDTO.setShift( entity.getShift().name() );
        }

        return workerAssignmentDTO;
    }

    private String entityIdAssignmentId(WorkerAssignment workerAssignment) {
        if ( workerAssignment == null ) {
            return null;
        }
        WorkerAssignmentId id = workerAssignment.getId();
        if ( id == null ) {
            return null;
        }
        String assignmentId = id.getAssignmentId();
        if ( assignmentId == null ) {
            return null;
        }
        return assignmentId;
    }

    private String entityIdWorkerId(WorkerAssignment workerAssignment) {
        if ( workerAssignment == null ) {
            return null;
        }
        WorkerAssignmentId id = workerAssignment.getId();
        if ( id == null ) {
            return null;
        }
        String workerId = id.getWorkerId();
        if ( workerId == null ) {
            return null;
        }
        return workerId;
    }
}
