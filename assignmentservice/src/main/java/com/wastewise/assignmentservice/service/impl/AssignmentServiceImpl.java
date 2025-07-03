package com.wastewise.assignmentservice.service.impl;

import com.wastewise.assignmentservice.client.VehicleClient;

import com.wastewise.assignmentservice.dto.AssignmentDTO;
import com.wastewise.assignmentservice.entity.Assignment;
import com.wastewise.assignmentservice.exception.DuplicateAssignmentException;
import com.wastewise.assignmentservice.exception.ResourceNotFoundException;
import com.wastewise.assignmentservice.repository.AssignmentRepository;
import com.wastewise.assignmentservice.service.AssignmentService;
import com.wastewise.assignmentservice.utility.IdGenerator;
import com.wastewise.assignmentservice.enums.VehicleStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository repository;
    private final ModelMapper modelMapper;
    private final IdGenerator idGenerator;
/*

    @Override
    public AssignmentDTO createAssignment(AssignmentDTO dto) {
        //  prevent duplicate vehicle-route assignment
        boolean exists = repository.existsByVehicleIdAndRouteId(dto.getVehicleId(), dto.getRouteId());
        if (exists) {
            throw new DuplicateAssignmentException("This vehicle is already assigned to the selected route.");
        }

        Assignment assignment = modelMapper.map(dto, Assignment.class);
        assignment.setAssignmentId(idGenerator.generateAssignmentId());
        Assignment saved = repository.save(assignment);
        log.info("Created assignment with ID: {}", saved.getAssignmentId());
        return modelMapper.map(saved, AssignmentDTO.class);
    }
*/

private final VehicleClient vehicleClient;

    public AssignmentDTO createAssignment(AssignmentDTO dto) {
        // Prevent duplicate vehicle-route assignment
        boolean exists = repository.existsByVehicleIdAndRouteId(dto.getVehicleId(), dto.getRouteId());
        if (exists) {
            throw new DuplicateAssignmentException("This vehicle is already assigned to the selected route.");
        }

        Assignment assignment = modelMapper.map(dto, Assignment.class);
        assignment.setAssignmentId(idGenerator.generateAssignmentId());
        Assignment saved = repository.save(assignment);
        log.info("Created assignment with ID: {}", saved.getAssignmentId());

        // Update vehicle status to UNAVAILABLE

        log.info("Calling Vehicle Service to update status for vehicle ID: {}", dto.getVehicleId());
        try {
            vehicleClient.updateVehicleStatus(dto.getVehicleId(), VehicleStatus.UNAVAILABLE);
            log.info("Vehicle status updated to UNAVAILABLE for vehicle ID: {}", dto.getVehicleId());
        } catch (Exception e) {
            log.error("Failed to update vehicle status for vehicle ID: {}", dto.getVehicleId(), e);
            // Optional: throw a custom exception or handle fallback logic
        }

        return modelMapper.map(saved, AssignmentDTO.class);
    }


    @Override
    public AssignmentDTO getAssignmentById(String id) {
        Assignment assignment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with ID: " + id));
        return modelMapper.map(assignment, AssignmentDTO.class);
    }

    @Override
    public List<AssignmentDTO> getAllAssignments() {
        return repository.findAll().stream()
                .map(a -> modelMapper.map(a, AssignmentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAssignment(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Assignment not found with ID: " + id);
        }
        repository.deleteById(id);
        log.info("Deleted assignment with ID: {}", id);
    }
    @Override
    public AssignmentDTO updateAssignment(String id, AssignmentDTO dto) {
        Assignment existing = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with ID: " + id));

        existing.setVehicleId(dto.getVehicleId());
        existing.setRouteId(dto.getRouteId());
        existing.setDateAssigned(dto.getDateAssigned());

        Assignment updated = repository.save(existing);
        return modelMapper.map(updated, AssignmentDTO.class);
    }

    @Override
    public List<AssignmentDTO> getAssignmentsByRouteId(String routeId) {
        List<Assignment> assignments = repository.findByRouteId(routeId);
        return assignments.stream()
                .map(assignment -> modelMapper.map(assignment, AssignmentDTO.class))
                .collect(Collectors.toList());
    }


}