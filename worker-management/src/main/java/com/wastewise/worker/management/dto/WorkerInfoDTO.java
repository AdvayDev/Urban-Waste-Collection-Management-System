package com.wastewise.worker.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerInfoDTO {
    private String workerId;
    private String name;
}
