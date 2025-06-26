package com.wastewise.workermanagement.controller;

import com.wastewise.workermanagement.dto.WorkerCreateDTO;
import com.wastewise.workermanagement.dto.WorkerDTO;
import com.wastewise.workermanagement.dto.WorkerInfoDTO;
import com.wastewise.workermanagement.dto.WorkerUpdateDTO;
import com.wastewise.workermanagement.enums.WorkerStatus;
import com.wastewise.workermanagement.service.WorkerService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/wastewise/admin/workers")
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    /**
     *  Accessed by: Admin
     * Creating a new worker
     * @param dto takes dto of worker as input. (name, contactNumber, contactEmail, roleId, Status)
     * @return string message for confirmation of successful or failed execution
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<String> createWorker(@Valid @RequestBody WorkerCreateDTO dto) {
        log.info("Creating a new worker profile");
        return ResponseEntity.ok(workerService.createWorker(dto));
    }

    /**
     *  Accessed by: Admin
     * finding all the workers
     * @return a list of workerDTO (workerId, name, contactNumber, contactEmail, roleId, Status
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<WorkerDTO>> findAllWorkers(){
        log.info("Fetching all the workers");
        return ResponseEntity.ok(workerService.getAllWorkers());
    }

    /**
     *  Accessed by: Admin, sanitary worker
     * getting worker by workerId
     * @param id workerId is passed as a prameter
     * @return workerDTO object
     */
    @PreAuthorize("hasAnyRole('ADMIN','SANITARY_WORKER')")
    @GetMapping("/{id}")
    public ResponseEntity<WorkerDTO> getWorker(@PathVariable String id) {
        log.info("Finding worker with id {}",id);
        WorkerDTO worker = workerService.getWorker(id);
        return ResponseEntity.ok(worker);
    }

    /**
     *  Accessed by: Admin
     * getting all the workerIds irrespective of role and status
     * @return List of all the workerIds
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ids")
    public ResponseEntity<List<String>> getAllWorkerIds() {
        log.info("fetching the list of all workerIds and names of workers that are AVAILABLE and SANITARY_WORKER");
        return new ResponseEntity<>(workerService.getWorkerIds(), HttpStatus.OK);
    }

    /**
     *  Accessed by: Admin, Scheduler
     * finding all the sanitary workers with status Available
     * @return list of workerInfoDTOs with status is available
     */
    @PreAuthorize("hasAnyRole('ADMIN','SCHEDULER')")
    @GetMapping("/ids/available")
    public ResponseEntity<List<WorkerInfoDTO>> getAvailableWorkerIds() {
        log.info("fetching all the available workers");
        return ResponseEntity.ok(workerService.getAllAvailableWorkerIds());
    }

    /**
     *  Accessed by: Admin
     * Updating worker information
     * @param id WorkerId is passed as a parameter to pass the object
     * @param dto WorkerUpdateDTO is passed (name, contactNumber, contactEmail, roleId, status)
     * @return String message sharing the execution status of the
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateWorker(@PathVariable String id,
                                                        @Valid @RequestBody WorkerUpdateDTO dto) {
        log.info("updating worker with id {}", id);
        return ResponseEntity.ok(workerService.updateWorker(id, dto));
    }

    /**
     * Accessed by: Admin, Scheduler
     * updating worker status
     * @param workerId workerId is passed to find the worker
     * @param workerStatus worker status is passed as request body
     * @return string message confirming the updating of status
     */
    @PreAuthorize("hasAnyRole('ADMIN','SCHEDULER')")
    @PatchMapping("/status/{workerId}")
    public ResponseEntity<String> updateWorkerStatus(@PathVariable String workerId, @RequestBody WorkerStatus workerStatus){
        log.info("Updating the status of worker with id {} to status {}", workerId, workerStatus);
        return ResponseEntity.ok(workerService.changeWorkerStatus(workerId, workerStatus));
    }
}
