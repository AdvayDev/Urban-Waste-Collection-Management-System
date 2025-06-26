package com.wastewise.worker.management.controller;

import com.wastewise.worker.management.dto.WorkerCreateDTO;
import com.wastewise.worker.management.dto.WorkerDTO;
import com.wastewise.worker.management.dto.WorkerInfoDTO;
import com.wastewise.worker.management.dto.WorkerUpdateDTO;
import com.wastewise.worker.management.enums.WorkerStatus;
import com.wastewise.worker.management.service.serviceimpl.WorkerServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/wastewise/admin/workers")
public class WorkerController {

    private final WorkerServiceImpl workerServiceImpl;

    public WorkerController(WorkerServiceImpl workerServiceImpl) {
        this.workerServiceImpl = workerServiceImpl;
    }

    /**
     * Creating a new worker
     * @param dto takes dto of worker as input. (name, contactNumber, contactEmail, roleId, Status)
     * @return string message for confirmation of successful or failed execution
     */
    @PostMapping
    public ResponseEntity<String> createWorker(@Valid @RequestBody WorkerCreateDTO dto) {
        log.info("Creating a new worker profile");
        return ResponseEntity.ok(workerServiceImpl.createWorker(dto));
    }

    /**
     * finding all the workers
     * @return a list of workerDTO (workerId, name, contactNumber, contactEmail, roleId, Status
     */
    @GetMapping
    public ResponseEntity<List<WorkerDTO>> findAllWorkers(){
        log.info("Fetching all the workers");
        return ResponseEntity.ok(workerServiceImpl.getAllWorkers());
    }

    /**
     * getting worker by workerId
     * @param id workerId is passed as a prameter
     * @return workerDTO object
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkerDTO> getWorker(@PathVariable String id) {
        log.info("Finding worker with id {}",id);
        WorkerDTO worker = workerServiceImpl.getWorker(id);
        return ResponseEntity.ok(worker);
    }

    /**
     * getting all the workerIds irrespective of role and status
     * @return List of all the workerIds
     */
    @GetMapping("/ids")
    public ResponseEntity<List<String>> getAllWorkerIds() {
        log.info("fetching the list of all workerIds");
        return new ResponseEntity<>(workerServiceImpl.getWorkerIds(), HttpStatus.OK);
    }

    /**
     * finding all the workers with status Available
     * @return list of workerInfoDTOs with status is available
     */
    @GetMapping("/ids/available")
    public ResponseEntity<List<WorkerInfoDTO>> getAvailableWorkerIds() {
        log.info("fetching all the available workers");
        return ResponseEntity.ok(workerServiceImpl.getAllAvailableWorkerIds());
    }

    /**
     * Updating worker information
     * @param id WorkerId is passed as a parameter to pass the object
     * @param dto WorkerUpdateDTO is passed (name, contactNumber, contactEmail, roleId, status)
     * @return String message sharing the execution status of the
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateWorker(@PathVariable String id,
                                                        @Valid @RequestBody WorkerUpdateDTO dto) {
        log.info("updating worker with id {}", id);
        return ResponseEntity.ok(workerServiceImpl.updateWorker(id, dto));
    }

    /**
     * updating worker status
     * @param workerId workerId is passed to find the worker
     * @param workerStatus worker status is passed as request body
     * @return string message confirming the updating of status
     */
    @PatchMapping("/status/{workerId}")
    public ResponseEntity<String> updateWorkerStatus(@PathVariable String workerId, @RequestBody WorkerStatus workerStatus){
        log.info("Updating the status of worker with id {} to status {}", workerId, workerStatus);
        return ResponseEntity.ok(workerServiceImpl.changeWorkerStatus(workerId, workerStatus));
    }
}
