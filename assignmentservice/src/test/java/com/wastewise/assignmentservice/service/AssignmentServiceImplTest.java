
package com.wastewise.assignmentservice.service;

import com.wastewise.assignmentservice.dto.AssignmentDTO;
import com.wastewise.assignmentservice.entity.Assignment;
import com.wastewise.assignmentservice.exception.DuplicateAssignmentException;
import com.wastewise.assignmentservice.exception.ResourceNotFoundException;
import com.wastewise.assignmentservice.repository.AssignmentRepository;
import com.wastewise.assignmentservice.service.impl.AssignmentServiceImpl;
import com.wastewise.assignmentservice.utility.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceImplTest {

    @Mock
    private AssignmentRepository repository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private AssignmentServiceImpl service;

    private Assignment assignment;
    private AssignmentDTO assignmentDTO;

    @BeforeEach
    void setUp() {
        assignment = Assignment.builder()
                .assignmentId("A001")
                .vehicleId("V001")
                .routeId("R001")
                .dateAssigned(LocalDate.now())
                .build();

        assignmentDTO = AssignmentDTO.builder()
                .assignmentId("A001")
                .vehicleId("V001")
                .routeId("R001")
                .dateAssigned(LocalDate.now())
                .build();
    }

    @Test
    void testCreateAssignment() {
        when(repository.existsByVehicleIdAndRouteId(any(), any())).thenReturn(false);
        when(idGenerator.generateAssignmentId()).thenReturn("A001");
        when(modelMapper.map(any(AssignmentDTO.class), eq(Assignment.class))).thenReturn(assignment);
        when(modelMapper.map(any(Assignment.class), eq(AssignmentDTO.class))).thenReturn(assignmentDTO);
        when(repository.save(any(Assignment.class))).thenReturn(assignment);

        AssignmentDTO result = service.createAssignment(assignmentDTO);

        assertNotNull(result);
        assertEquals("A001", result.getAssignmentId());
    }

    @Test
    void testCreateAssignment_Duplicate() {
        when(repository.existsByVehicleIdAndRouteId(any(), any())).thenReturn(true);

        assertThrows(DuplicateAssignmentException.class, () -> service.createAssignment(assignmentDTO));
    }

    @Test
    void testGetAssignmentById() {
        when(repository.findById("A001")).thenReturn(Optional.of(assignment));
        when(modelMapper.map(any(Assignment.class), eq(AssignmentDTO.class))).thenReturn(assignmentDTO);

        AssignmentDTO result = service.getAssignmentById("A001");

        assertNotNull(result);
        assertEquals("A001", result.getAssignmentId());
    }

    @Test
    void testGetAssignmentById_NotFound() {
        when(repository.findById("A001")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getAssignmentById("A001"));
    }

    @Test
    void testGetAllAssignments() {
        when(repository.findAll()).thenReturn(List.of(assignment));
        when(modelMapper.map(any(Assignment.class), eq(AssignmentDTO.class))).thenReturn(assignmentDTO);

        List<AssignmentDTO> result = service.getAllAssignments();

        assertEquals(1, result.size());
    }

    @Test
    void testDeleteAssignment() {
        when(repository.existsById("A001")).thenReturn(true);

        service.deleteAssignment("A001");

        verify(repository, times(1)).deleteById("A001");
    }

    @Test
    void testDeleteAssignment_NotFound() {
        when(repository.existsById("A001")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.deleteAssignment("A001"));
    }

    @Test
    void testUpdateAssignment() {
        when(repository.findById("A001")).thenReturn(Optional.of(assignment));
        when(repository.save(any(Assignment.class))).thenReturn(assignment);
        when(modelMapper.map(any(Assignment.class), eq(AssignmentDTO.class))).thenReturn(assignmentDTO);

        AssignmentDTO result = service.updateAssignment("A001", assignmentDTO);

        assertEquals("A001", result.getAssignmentId());
    }

    @Test
    void testUpdateAssignment_NotFound() {
        when(repository.findById("A001")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateAssignment("A001", assignmentDTO));
    }
}
