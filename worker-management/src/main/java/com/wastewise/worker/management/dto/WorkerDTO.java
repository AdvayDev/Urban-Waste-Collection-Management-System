package com.wastewise.worker.management.dto;

import com.wastewise.worker.management.enums.WorkerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerDTO {
    private String workerId;
    private String name;
    private String contactNumber;
    private String contactEmail;
    private WorkerStatus workerStatus;
}
