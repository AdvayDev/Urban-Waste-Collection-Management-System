package com.wastewise.workermanagement.dto;

import com.wastewise.workermanagement.enums.WorkerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerStatusDTO {
    private String workerId;
    private WorkerStatus status;
}
