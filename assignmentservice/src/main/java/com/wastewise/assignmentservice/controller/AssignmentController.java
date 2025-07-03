package com.wastewise.assignmentservice.controller;

import com.wastewise.assignmentservice.dto.AssignmentDTO;
import com.wastewise.assignmentservice.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;

import org.springframework.validation.annotation.Validated;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("wastewise/admin/assignments")
@RequiredArgsConstructor
@Slf4j
public class AssignmentController {

    private final AssignmentService assignmentService;


    /**
     * Accessed by Admin
     * Creates a new assignment.
     *
     * @param dto the assignment data to create
     * @return ResponseEntity containing the created AssignmentDTO and HTTP status 201 (Created)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    //added valid annotation
    public ResponseEntity<AssignmentDTO> create( @Valid @RequestBody AssignmentDTO dto) {
        log.info("Creating new assignment");
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.createAssignment(dto));
    }

    /**
     * Accessed by Admin
     * Retrieves an assignment by its ID.
     *
     * @param id the ID of the assignment
     * @return ResponseEntity containing the AssignmentDTO and HTTP status 200 (OK)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AssignmentDTO> getById(@PathVariable String id) {
        log.info("Fetching assignment with ID: {}", id);
        return ResponseEntity.ok(assignmentService.getAssignmentById(id));
    }

    /**
     * Accessed by Admin
     * Retrieves all assignments.
     *
     * @return ResponseEntity containing a list of AssignmentDTOs and HTTP status 200 (OK)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AssignmentDTO>> getAll() {
        log.info("Fetching all assignments");
        return ResponseEntity.ok(assignmentService.getAllAssignments());
    }

    /**
     * Accessed by Admin
     * Deletes an assignment by its ID.
     *
     * @param id the ID of the assignment to delete
     * @return ResponseEntity with a success message and HTTP status 200 (OK)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        log.info("Deleting assignment with ID: {}", id);
        assignmentService.deleteAssignment(id);
        return ResponseEntity.ok("Assignment deleted successfully.");
    }

    /**
     * Accessed by Admin
     * Updates an existing assignment.
     *
     * @param id the ID of the assignment to update
     * @param dto the updated assignment data
     * @return ResponseEntity containing the updated AssignmentDTO and HTTP status 200 (OK)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AssignmentDTO> update(@PathVariable String id, @RequestBody AssignmentDTO dto) {
        log.info("Updating assignment with ID: {}", id);
        return ResponseEntity.ok(assignmentService.updateAssignment(id, dto));
    }

    /**
     * Accessed by Admin
     * gets all the assignments assigned to route id
     * @param routeId of route
     * @return list of assignments shared by
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<AssignmentDTO>> getAssignmentsByRouteId(@PathVariable String routeId) {
        log.info("Fetching assignments for route ID: {}", routeId);
        return ResponseEntity.ok(assignmentService.getAssignmentsByRouteId(routeId));
    }

}

