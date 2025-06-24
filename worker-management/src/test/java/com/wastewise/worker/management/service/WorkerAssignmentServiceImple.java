package com.wastewise.worker.management.service;

import com.wastewise.worker.management.dto.WorkerAssignmentDTO;
import com.wastewise.worker.management.enums.Shift;
import com.wastewise.worker.management.enums.WorkerStatus;
import com.wastewise.worker.management.exception.*;
import com.wastewise.worker.management.mapper.WorkerAssignmentMapper;
import com.wastewise.worker.management.model.*;
import com.wastewise.worker.management.repository.WorkerAssignmentRepository;
import com.wastewise.worker.management.repository.WorkerRepository;
import com.wastewise.worker.management.service.serviceimpl.WorkerAssignmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkerAssignmentServiceImplTest {

    @Mock
    private WorkerAssignmentRepository workerAssignmentRepository;

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private WorkerAssignmentMapper workerAssignmentMapper;

    @InjectMocks
    private WorkerAssignmentServiceImpl service;

    private Worker worker;
    private WorkerAssignmentDTO dto;
    private WorkerAssignment assignment;

    @BeforeEach
    void setUp() {
        worker = new Worker();
        worker.setWorkerId("W001");
        worker.setWorkerStatus(WorkerStatus.AVAILABLE);

        dto = new WorkerAssignmentDTO("A001", "W001", "Z001","Z001-R001", "DAY");

        assignment = new WorkerAssignment();
        assignment.setId(new WorkerAssignmentId("A001", "W001"));
        assignment.setWorker(worker);
        assignment.setCreatedDate(LocalDateTime.now());
    }


    @Test
    void testFindAllWorkerAssignments() {
        when(workerAssignmentRepository.findAll()).thenReturn(List.of(assignment));
        when(workerAssignmentMapper.toDTO(assignment)).thenReturn(dto);

        List<WorkerAssignmentDTO> result = service.findAllWorkerAssignments();
        assertEquals(1, result.size());
    }


    @Test
    void testFindWorkerAssignment_NotFound() {
        // Arrange
        String workerId = "W999";
        Mockito.when(workerAssignmentRepository.findByIdWorkerId(workerId)).thenReturn(null);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            service.findWorkerAssignment(workerId);
        });
    }

    @Test
    void testFindWorkerAssignment_Success() {
        // Arrange
        String workerId = "W123";
        WorkerAssignmentId assignmentId = new WorkerAssignmentId();
        assignmentId.setWorkerId(workerId);
        assignmentId.setAssignmentId("A001");

        WorkerAssignment assignment = new WorkerAssignment();
        assignment.setId(assignmentId);
        assignment.setRouteId("Z001-R001");
        assignment.setZoneId("Z001");
        assignment.setShift(Shift.DAY); // Assuming Shift is an enum

        Mockito.when(workerAssignmentRepository.findByIdWorkerId(workerId)).thenReturn(assignment);

        // Act
        WorkerAssignmentDTO result = service.findWorkerAssignment(workerId);

        // Assert
        assertNotNull(result);
        assertEquals("A001", result.getAssignmentId());
        assertEquals("W123", result.getWorkerId());
        assertEquals("Z001-R001", result.getRouteId());
        assertEquals("Z001", result.getZoneId());
        assertEquals("DAY", result.getShift());
    }


    @Test
    void testAssignWorkerToAssignment_Success() {
        when(workerAssignmentRepository.findByIdAssignmentId("A001")).thenReturn(List.of());
        when(workerRepository.findById("W001")).thenReturn(Optional.of(worker));

        String result = service.assignWorkerToAssignment("A001", "W001", dto);
        assertTrue(result.contains("Worker assigned successfully"));
    }

    @Test
    void testAssignWorkerToAssignment_TooManyWorkers() {
        when(workerAssignmentRepository.findByIdAssignmentId("A001")).thenReturn(List.of(assignment, assignment));

        assertThrows(WorkersAlreadyAssignedException.class,
                () -> service.assignWorkerToAssignment("A001", "W001", dto));
    }

    @Test
    void testAssignWorkerToAssignment_WorkerNotFound() {
        when(workerAssignmentRepository.findByIdAssignmentId("A001")).thenReturn(List.of());
        when(workerRepository.findById("W001")).thenReturn(Optional.empty());

        assertThrows(WorkerNotFoundException.class,
                () -> service.assignWorkerToAssignment("A001", "W001", dto));
    }

    @Test
    void testAssignWorkerToAssignment_WorkerNotAvailable() {
        worker.setWorkerStatus(WorkerStatus.OCCUPIED);
        when(workerAssignmentRepository.findByIdAssignmentId("A001")).thenReturn(List.of());
        when(workerRepository.findById("W001")).thenReturn(Optional.of(worker));

        assertThrows(WorkersAlreadyAssignedException.class,
                () -> service.assignWorkerToAssignment("A001", "W001", dto));
    }

    @Test
    void testUpdateSingleWorkerAssignment_Success() {
        Worker newWorker = new Worker();
        newWorker.setWorkerId("W002");
        newWorker.setWorkerStatus(WorkerStatus.AVAILABLE);

        when(workerAssignmentRepository.existsById(any())).thenReturn(true, false);
        when(workerRepository.findById("W002")).thenReturn(Optional.of(newWorker));
        when(workerAssignmentRepository.findById(any())).thenReturn(Optional.of(assignment));
        when(workerRepository.findById("W001")).thenReturn(Optional.of(worker));

        String result = service.updateSingleWorkerAssignment("A001", "W001", "W002");
        assertTrue(result.contains("Worker assignment updated successfully"));
    }

    @Test
    void testUpdateSingleWorkerAssignment_OldNotAssigned() {
        when(workerAssignmentRepository.existsById(any())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateSingleWorkerAssignment("A001", "W001", "W002"));
    }

    @Test
    void testUpdateSingleWorkerAssignment_NewAlreadyAssigned() {
        when(workerAssignmentRepository.existsById(any())).thenReturn(true, true);

        assertThrows(IllegalStateException.class,
                () -> service.updateSingleWorkerAssignment("A001", "W001", "W002"));
    }

    @Test
    void testUpdateSingleWorkerAssignment_NewWorkerNotFound() {
        when(workerAssignmentRepository.existsById(any())).thenReturn(true, false);
        when(workerRepository.findById("W002")).thenReturn(Optional.empty());

        assertThrows(WorkerNotFoundException.class,
                () -> service.updateSingleWorkerAssignment("A001", "W001", "W002"));
    }

    @Test
    void testUpdateSingleWorkerAssignment_NewWorkerNotAvailable() {
        Worker newWorker = new Worker();
        newWorker.setWorkerId("W002");
        newWorker.setWorkerStatus(WorkerStatus.OCCUPIED);

        when(workerAssignmentRepository.existsById(any())).thenReturn(true, false);
        when(workerRepository.findById("W002")).thenReturn(Optional.of(newWorker));

        assertThrows(IllegalStateException.class,
                () -> service.updateSingleWorkerAssignment("A001", "W001", "W002"));
    }

    @Test
    void testUpdateBothWorkerAssignments_Success() {
        Worker w1 = new Worker(); w1.setWorkerId("W003"); w1.setWorkerStatus(WorkerStatus.AVAILABLE);
        Worker w2 = new Worker(); w2.setWorkerId("W004"); w2.setWorkerStatus(WorkerStatus.AVAILABLE);

        when(workerAssignmentRepository.findAllById(any())).thenReturn(List.of(assignment, assignment));
        when(workerRepository.findAllById(any())).thenReturn(List.of(w1, w2));

        String result = service.updateBothWorkerAssignments("A001", "W001", "W002", "W003", "W004");
        assertTrue(result.contains("Both worker assignments updated successfully"));
    }

    @Test
    void testUpdateBothWorkerAssignments_OldAssignmentsMissing() {
        when(workerAssignmentRepository.findAllById(any())).thenReturn(List.of(assignment));

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateBothWorkerAssignments("A001", "W001", "W002", "W003", "W004"));
    }

    @Test
    void testUpdateBothWorkerAssignments_NewWorkersMissing() {
        when(workerAssignmentRepository.findAllById(any())).thenReturn(List.of(assignment, assignment));
        when(workerRepository.findAllById(any())).thenReturn(List.of(worker));

        assertThrows(WorkerNotFoundException.class,
                () -> service.updateBothWorkerAssignments("A001", "W001", "W002", "W003", "W004"));
    }

    @Test
    void testUpdateBothWorkerAssignments_NewWorkerNotAvailable() {
        Worker w1 = new Worker(); w1.setWorkerId("W003"); w1.setWorkerStatus(WorkerStatus.AVAILABLE);
        Worker w2 = new Worker(); w2.setWorkerId("W004"); w2.setWorkerStatus(WorkerStatus.OCCUPIED);

        when(workerAssignmentRepository.findAllById(any())).thenReturn(List.of(assignment, assignment));
        when(workerRepository.findAllById(any())).thenReturn(List.of(w1, w2));

        assertThrows(IllegalStateException.class,
                () -> service.updateBothWorkerAssignments("A001", "W001", "W002", "W003", "W004"));
    }

    @Test
    void testDeleteWorkerAssignment_Success() {
        when(workerAssignmentRepository.findByIdAssignmentId("A001")).thenReturn(List.of(assignment));
        when(workerRepository.findAllById(any())).thenReturn(List.of(worker));

        String result = service.deleteWorkerAssignment("A001");
        assertTrue(result.contains("Deleted assignments and updated worker statuses"));
    }

    @Test
    void testDeleteWorkerAssignment_NoAssignmentsFound() {
        when(workerAssignmentRepository.findByIdAssignmentId("A001")).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> service.deleteWorkerAssignment("A001"));
    }
}

