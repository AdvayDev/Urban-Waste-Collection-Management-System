package com.wastewise.worker.management.service.serviceimpl;

import com.wastewise.worker.management.dto.WorkerCreateDTO;
import com.wastewise.worker.management.dto.WorkerDTO;
import com.wastewise.worker.management.dto.WorkerInfoDTO;
import com.wastewise.worker.management.dto.WorkerUpdateDTO;
import com.wastewise.worker.management.enums.WorkerStatus;
import com.wastewise.worker.management.exception.ContactInformationUsedException;
import com.wastewise.worker.management.exception.WorkerNotFoundException;
import com.wastewise.worker.management.mapper.WorkerMapper;
import com.wastewise.worker.management.model.Worker;
import com.wastewise.worker.management.repository.WorkerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class WorkerServiceImpl implements com.wastewise.worker.management.service.WorkerService {

    private final WorkerRepository workerRepository;
    private final WorkerMapper workerMapper;

    public WorkerServiceImpl(WorkerRepository workerRepository,
                             WorkerMapper workerMapper) {
        this.workerRepository = workerRepository;
        this.workerMapper = workerMapper;
    }

    public String generateWorkerId() {
        long count = workerRepository.countAll();
        return String.format("W%03d", count + 1);
    }

    /**
     * Creates a new worker in the system, takes dto of workerCreateDTO
     *
     * @param dto of workerCreateDTO(name, contactNumber, contactEmail, roleId, Status)
     * @return worker creation message
     */
    @Transactional
    public String createWorker(WorkerCreateDTO dto) {
        String id = generateWorkerId();

        log.info("Creating new worker: {}", id);
        if(workerRepository.existsByContactNumber(dto.getContactNumber())){
            throw new ContactInformationUsedException("The given contact number is already being used, please enter a different number");
        }
        if(dto.getContactEmail() != null && workerRepository.existsByContactEmail(dto.getContactEmail())){
            throw new ContactInformationUsedException("The given contact email is already being used, please enter a different email");
        }

        Worker worker = workerMapper.toEntity(dto);
        WorkerStatus status = WorkerStatus.valueOf(dto.getWorkerStatus());
        worker.setWorkerStatus(status);
        worker.setWorkerId(id);
        worker.setCreatedDate(LocalDateTime.now());
        worker.setCreatedBy("000"); //To be updated using workerId in token
        workerRepository.save(worker);
        return "Created worker with id " + worker.getWorkerId();
    }

    /**
     * fetching the list of all workers
     * @return list of workerDTO (id, name, contactNumber, contactEmail, roleId, status)
     */
    public List<WorkerDTO> getAllWorkers(){
        log.info("Fetching all the workers' information");
        return workerRepository.findAll()
                .stream()
                .map(workerMapper::toDTO)
                .toList();
    }

    /**
     * Retrieves a worker by ID.
     *
     * @return workerDTO object that is consist of worker id, name, contact details, status and roleId
     */
    public WorkerDTO getWorker(String id) throws WorkerNotFoundException {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new WorkerNotFoundException("Worker with id " + id + " does not exist"));
        log.info("fetching worker with id {}", id);
        return workerMapper.toDTO(worker);
    }

    /**
     * Retrieves all worker IDs.
     *
     * @return list of all worker Ids and Names
     */
    public List<String> getWorkerIds() {
        log.info("fetching all the worker Ids");
        return workerRepository.findAllWorkerId();
    }

    /**
     * Retrieves all available worker IDs (based on status).
     *
     * @return list of all workers with status = "Available"
     */
    public List<WorkerInfoDTO> getAllAvailableWorkerIds() {
        log.info("fetching all the worker Ids with status 'AVAILABLE'");
        return workerRepository.findAvailableWorkersByRole();
    }

    /**
     * Updates an existing worker's information.
     *
     * @return dto of WorkerUpdateDTO
     */
    @Transactional
    public String updateWorker(String id, WorkerUpdateDTO dto) {
        log.info("fetching worker with id {} to update", id);
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new WorkerNotFoundException("Worker with id " + id + " does not exist"));
        if(workerRepository.existsByContactNumberAndWorkerIdNot(dto.getContactNumber(), id)){
            throw new ContactInformationUsedException("Contact number already being used by another worker");
        }
        if(workerRepository.existsByContactEmailAndWorkerIdNot(dto.getContactEmail(),id)){
            throw new ContactInformationUsedException("Contact email is already being used by another worker");
        }

        log.info("updating details of the worker with id {}",id);
        workerMapper.updateWorkerFromDTO(dto, worker);
        WorkerStatus status = WorkerStatus.valueOf(dto.getWorkerStatus());
        worker.setWorkerStatus(status);
        worker.setUpdatedDate(LocalDateTime.now());
        worker.setUpdatedBy("000"); //To be updated via workerId in JWT token
        workerRepository.save(worker);
        return "Updated worker with id "+worker.getWorkerId();
    }

    /**
     * Changing the status of worker
     * @param id workerId of the worker whose status needs to be changed
     * @param workerStatus status of the worker to which it needs
     * @return String message confirming the changing of status
     */
    @Transactional
    public String changeWorkerStatus(String id, WorkerStatus workerStatus){
        log.info("fetching the worker with id {} to update status", id);
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new WorkerNotFoundException("Worker with id " + id + " does not exist"));
        log.info("Changing the status of worker to {}",workerStatus);
        worker.setWorkerStatus(workerStatus);
        workerRepository.save(worker);

        return "Status of worker with id "+ id + " changed successfully";
    }
}