package com.wastewise.worker.management.dto;

import com.wastewise.worker.management.enums.WorkerStatus;
import lombok.Data;

@Data
public class WorkerDTO {
    private String workerId;
    private String name;
    private String contactNumber;
    private String contactEmail;
    private WorkerStatus workerStatus;
}
