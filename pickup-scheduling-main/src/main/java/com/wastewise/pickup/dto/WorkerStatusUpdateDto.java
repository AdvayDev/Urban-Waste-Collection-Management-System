package com.wastewise.pickup.dto;

import com.wastewise.pickup.model.enums.WorkerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for worker status updates sent to Worker Service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkerStatusUpdateDto {
    private String workerId; // Worker ID to update
    private WorkerStatus status; // Expected values: "OCCUPIED" or "AVAILABLE"
}