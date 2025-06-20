package com.wastewise.worker.management.service;

import com.wastewise.worker.management.dto.WorkerCreateDTO;
import com.wastewise.worker.management.dto.WorkerDTO;
import com.wastewise.worker.management.dto.WorkerInfoDTO;
import com.wastewise.worker.management.dto.WorkerUpdateDTO;
import com.wastewise.worker.management.enums.WorkerStatus;

import java.util.List;

public interface WorkerService {
    String createWorker(WorkerCreateDTO dto);

    List<WorkerDTO> getAllWorkers();

    WorkerDTO getWorker(String id);

    List<String> getWorkerIds();

    List<WorkerInfoDTO> getAllAvailableWorkerIds();

    String updateWorker(String id, WorkerUpdateDTO dto);

    String changeWorkerStatus(String id, WorkerStatus workerStatus);

}
