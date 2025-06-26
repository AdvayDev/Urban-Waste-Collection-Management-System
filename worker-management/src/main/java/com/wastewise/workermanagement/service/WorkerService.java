package com.wastewise.workermanagement.service;

import com.wastewise.workermanagement.dto.WorkerCreateDTO;
import com.wastewise.workermanagement.dto.WorkerDTO;
import com.wastewise.workermanagement.dto.WorkerInfoDTO;
import com.wastewise.workermanagement.dto.WorkerUpdateDTO;
import com.wastewise.workermanagement.enums.WorkerStatus;

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
