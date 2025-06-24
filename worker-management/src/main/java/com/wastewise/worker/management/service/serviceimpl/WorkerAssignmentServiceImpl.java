package com.wastewise.worker.management.service.serviceimpl;

import com.wastewise.worker.management.dto.WorkerAssignmentDTO;
import com.wastewise.worker.management.enums.Shift;
import com.wastewise.worker.management.enums.WorkerStatus;
import com.wastewise.worker.management.exception.ResourceNotFoundException;
import com.wastewise.worker.management.exception.WorkerNotFoundException;
import com.wastewise.worker.management.exception.WorkersAlreadyAssignedException;
import com.wastewise.worker.management.mapper.WorkerAssignmentMapper;
import com.wastewise.worker.management.model.Worker;
import com.wastewise.worker.management.model.WorkerAssignment;
import com.wastewise.worker.management.model.WorkerAssignmentId;
import com.wastewise.worker.management.repository.WorkerAssignmentRepository;
import com.wastewise.worker.management.repository.WorkerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WorkerAssignmentServiceImpl implements com.wastewise.worker.management.service.WorkerAssignmentService {
    private final WorkerAssignmentRepository workerAssignmentRepository;

    private final WorkerAssignmentMapper workerAssignmentMapper;

    private final WorkerRepository workerRepository;

    public WorkerAssignmentServiceImpl(WorkerAssignmentRepository workerAssignmentRepository, WorkerRepository workerRepository, WorkerAssignmentMapper workerAssignmentMapper){
        this.workerAssignmentRepository = workerAssignmentRepository;
        this.workerRepository = workerRepository;
        this.workerAssignmentMapper = workerAssignmentMapper;
    }

    public List<WorkerAssignmentDTO> findAllWorkerAssignments(){
        return workerAssignmentRepository.findAll()
                .stream()
                .map(workerAssignmentMapper::toDTO)
                .toList();
    }

    /**
     * Method creates a record with assignmentId and workerId to indicate the assignment of worker to that assignment
     * @param assignmentId of assignment
     * @param workerId of worker
     * @return String message confirming the execution of the process
     */
    @Transactional
    public String assignWorkerToAssignment(String assignmentId, String workerId, WorkerAssignmentDTO dto){
        // Step 1: Fetch the worker
        if(workerAssignmentRepository.findByIdAssignmentId(assignmentId).size()>=2){
            throw new WorkersAlreadyAssignedException("The assignment has already two workers assigned to it, please update the assignment instead of assigning any new worker");
        }
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new WorkerNotFoundException("Worker not found with ID: " + workerId));

        if (worker.getWorkerStatus() != WorkerStatus.AVAILABLE) {
            throw new WorkersAlreadyAssignedException("Worker is not available for assignment");
        }

        WorkerAssignment assignment = new WorkerAssignment();
        assignment.setId(new WorkerAssignmentId(assignmentId, workerId));
        assignment.setWorker(worker);
        assignment.setCreatedDate(LocalDateTime.now());
        assignment.setRouteId(dto.getRouteId());
        assignment.setZoneId(dto.getZoneId());
        Shift shift = Shift.valueOf(dto.getShift());
        assignment.setShift(shift);

        workerAssignmentRepository.save(assignment);

        worker.setWorkerStatus(WorkerStatus.OCCUPIED);
        workerRepository.save(worker);

        log.info("Assigned worker {} to assignment {}", workerId, assignmentId);
        return "Worker assigned successfully";
    }

    /**
     * Method returns the assignment information of worker with ID
     * @param workerId of worker
     * @return WorkerAssignmentDTO consist of workerId, assignmentId, ZoneId, RouteId, Shift
     */
    public WorkerAssignmentDTO findWorkerAssignment(String workerId){
        log.info("Fetching worker assignment information of worker with Id {}",workerId);

        WorkerAssignmentDTO dto = new WorkerAssignmentDTO();

        WorkerAssignment assignment = workerAssignmentRepository.findByIdWorkerId(workerId);
        if(assignment == null){
            throw new ResourceNotFoundException("Worker assignment not found");
        }

        dto.setAssignmentId(assignment.getId().getAssignmentId());
        dto.setWorkerId(assignment.getId().getWorkerId());
        dto.setRouteId(assignment.getRouteId());
        dto.setZoneId(assignment.getZoneId());
        dto.setShift(assignment.getShift().name());

        return dto;
    }

    /**
     * Updating an assignment by changing assigned worker to the assignment. Also updates their status
     * @param assignmentId of assignment
     * @param newWorkerId of the worker who will replace the existing worker
     * @return String message confirming the completion of task
     */

    @Transactional
    public String updateSingleWorkerAssignment(String assignmentId, String oldWorkerId, String newWorkerId) {
        WorkerAssignmentId oldId = new WorkerAssignmentId(assignmentId, oldWorkerId);
        WorkerAssignmentId newId = new WorkerAssignmentId(assignmentId, newWorkerId);

        if (!workerAssignmentRepository.existsById(oldId)) {
            throw new ResourceNotFoundException("Old worker is not assigned to this assignment");
        }
        if (workerAssignmentRepository.existsById(newId)) {
            throw new IllegalStateException("New worker is already assigned to this assignment");
        }
        Worker newWorker = workerRepository.findById(newWorkerId)
                .orElseThrow(() -> new WorkerNotFoundException("New worker not found"));

        if (newWorker.getWorkerStatus() != WorkerStatus.AVAILABLE) {
            throw new IllegalStateException("Worker is not available");
        }
        WorkerAssignment oldWorkerAssignment =  workerAssignmentRepository.findById(oldId)
                .orElseThrow(() -> new ResourceNotFoundException("Worker assignment does not exist"));
        workerAssignmentRepository.deleteById(oldId);
        WorkerAssignment newAssignment = new WorkerAssignment();
        newAssignment.setWorker(newWorker);
        newAssignment.setId(newId);
        newAssignment.setCreatedDate(LocalDateTime.now());
        newAssignment.setZoneId(oldWorkerAssignment.getZoneId());
        newAssignment.setRouteId(oldWorkerAssignment.getRouteId());
        newAssignment.setCreatedDate(oldWorkerAssignment.getCreatedDate());
        newAssignment.setCreatedBy(oldWorkerAssignment.getCreatedBy());
        newAssignment.setShift(oldWorkerAssignment.getShift());
        newAssignment.setUpdatedDate(LocalDateTime.now());
        workerAssignmentRepository.save(newAssignment);

        Worker oldWorker = workerRepository.findById(oldWorkerId)
                .orElseThrow(() -> new WorkerNotFoundException("Old worker not found"));

        oldWorker.setWorkerStatus(WorkerStatus.AVAILABLE);
        newWorker.setWorkerStatus(WorkerStatus.OCCUPIED);
        workerRepository.saveAll(List.of(oldWorker, newWorker));

        return "Worker assignment updated successfully";
    }

    /**
     * Updating the assignment by replacing both the workers at same time
     * @param assignmentId of assignment
     * @param oldWorkerId1 of old worker 1
     * @param oldWorkerId2 of old worker 2
     * @param newWorkerId1 of new worker 1
     * @param newWorkerId2 of new worker 2
     * @return String message confirming the completion of method
     */
    @Transactional
    public String updateBothWorkerAssignments(String assignmentId,
                                              String oldWorkerId1, String oldWorkerId2,
                                              String newWorkerId1, String newWorkerId2) {
        List<WorkerAssignmentId> oldIds = List.of(
                new WorkerAssignmentId(assignmentId, oldWorkerId1),
                new WorkerAssignmentId(assignmentId, oldWorkerId2)
        );

        // Step 1: Fetch old assignments
        List<WorkerAssignment> oldAssignments = workerAssignmentRepository.findAllById(oldIds);
        if (oldAssignments.size() != 2) {
            throw new ResourceNotFoundException("One or both old assignments not found");
        }

        // Step 2: Fetch new workers
        List<String> newWorkerIds = List.of(newWorkerId1, newWorkerId2);
        List<Worker> newWorkers = workerRepository.findAllById(newWorkerIds);

        if (newWorkers.size() != 2) {
            throw new WorkerNotFoundException("One or both new workers not found");
        }

        for (Worker w : newWorkers) {
            if (w.getWorkerStatus() != WorkerStatus.AVAILABLE) {
                throw new IllegalStateException("Worker " + w.getWorkerId() + " is not available");
            }
        }

        // Delete old assignments
        workerAssignmentRepository.deleteAllById(oldIds);

        // Create new assignments with old values
        List<WorkerAssignment> newAssignments = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            WorkerAssignment oldAssignment = oldAssignments.get(i);
            String newWorkerId = newWorkerIds.get(i);
            Worker newWorker = newWorkers.get(i);

            WorkerAssignment newAssignment = new WorkerAssignment();
            newAssignment.setId(new WorkerAssignmentId(assignmentId, newWorkerId));
            newAssignment.setWorker(newWorker);
            newAssignment.setRouteId(oldAssignment.getRouteId());
            newAssignment.setZoneId(oldAssignment.getZoneId());
            newAssignment.setShift(oldAssignment.getShift());
            newAssignment.setCreatedBy(oldAssignment.getCreatedBy());
            newAssignment.setCreatedDate(oldAssignment.getCreatedDate());
            newAssignment.setUpdatedBy("000"); // To be modified from workerId of whoever is updating
            newAssignment.setUpdatedDate(LocalDateTime.now());

            newAssignments.add(newAssignment);
        }

        workerAssignmentRepository.saveAll(newAssignments);

        // Step 5: Update worker statuses
        List<String> oldWorkerIds = List.of(oldWorkerId1, oldWorkerId2);
        List<Worker> oldWorkers = workerRepository.findAllById(oldWorkerIds);

        for (Worker w : oldWorkers) {
            w.setWorkerStatus(WorkerStatus.AVAILABLE);
        }
        for (Worker w : newWorkers) {
            w.setWorkerStatus(WorkerStatus.OCCUPIED);
        }

        workerRepository.saveAll(oldWorkers);
        workerRepository.saveAll(newWorkers);

        log.info("Reassigned assignment {} from workers {} and {} to {} and {}",
                assignmentId, oldWorkerId1, oldWorkerId2, newWorkerId1, newWorkerId2);

        return "Both worker assignments updated successfully";
    }



    /**
     * Delete worker assignment and change the status of respective workers to AVAILABLE
     * @param assignmentId of assignment
     * @return String message indicating the successful deletion of tuple and changing of status of workers
     */
    @Transactional
    public String deleteWorkerAssignment(String assignmentId) {
        List<WorkerAssignment> assignments = workerAssignmentRepository.findByIdAssignmentId(assignmentId);

        if (assignments.isEmpty()) {
            throw new ResourceNotFoundException("No assignments found for assignmentId: " + assignmentId);
        }

        // Collect all worker IDs linked to this assignment
        List<String> workerIds = assignments.stream()
                .map(a -> a.getId().getWorkerId())
                .collect(Collectors.toList());

        workerAssignmentRepository.deleteAll(assignments);
        log.info("Deleted all assignments with assignmentId {}", assignmentId);

        List<Worker> workers = workerRepository.findAllById(workerIds);
        for (Worker worker : workers) {
            worker.setWorkerStatus(WorkerStatus.AVAILABLE);
        }
        workerRepository.saveAll(workers);
        log.info("Updated {} workers to status OCCUPIED", workers.size());

        return "Deleted assignments and updated worker statuses";
    }
}