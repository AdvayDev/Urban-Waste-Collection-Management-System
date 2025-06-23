package com.wastewise.assignmentservice.controller;

import com.wastewise.assignmentservice.dto.AssignmentDTO;
import com.wastewise.assignmentservice.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignments")
@RequiredArgsConstructor
@Slf4j
public class AssignmentController {

    private final AssignmentService assignmentService;


    /**
     * Creates a new assignment.
     *
     * @param dto the assignment data to create
     * @return ResponseEntity containing the created AssignmentDTO and HTTP status 201 (Created)
     */

    @PostMapping
    public ResponseEntity<AssignmentDTO> create(@RequestBody AssignmentDTO dto) {
        log.info("Creating new assignment");
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.createAssignment(dto));
    }

     /**
     * Retrieves an assignment by its ID.
     *
     * @param id the ID of the assignment
     * @return ResponseEntity containing the AssignmentDTO and HTTP status 200 (OK)
     */

    @GetMapping("/{id}")
    public ResponseEntity<AssignmentDTO> getById(@PathVariable String id) {
        log.info("Fetching assignment with ID: {}", id);
        return ResponseEntity.ok(assignmentService.getAssignmentById(id));
    }

     /**
     * Retrieves all assignments.
     *
     * @return ResponseEntity containing a list of AssignmentDTOs and HTTP status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<AssignmentDTO>> getAll() {
        log.info("Fetching all assignments");
        return ResponseEntity.ok(assignmentService.getAllAssignments());
    }

     /**
     * Deletes an assignment by its ID.
     *
     * @param id the ID of the assignment to delete
     * @return ResponseEntity with a success message and HTTP status 200 (OK)
     */
    
     
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        log.info("Deleting assignment with ID: {}", id);
        assignmentService.deleteAssignment(id);
        return ResponseEntity.ok("Assignment deleted successfully.");
    }

/**
     * Updates an existing assignment.
     *
     * @param id  the ID of the assignment to update
     * @param dto the updated assignment data
     * @return ResponseEntity containing the updated AssignmentDTO and HTTP status 200 (OK)
     */

    @PutMapping("/{id}")
    public ResponseEntity<AssignmentDTO> update(@PathVariable String id, @RequestBody AssignmentDTO dto) {
        log.info("Updating assignment with ID: {}", id);
        return ResponseEntity.ok(assignmentService.updateAssignment(id, dto));
    }
}

