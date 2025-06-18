package com.wastewise.worker.management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class WorkerAssignmentId implements Serializable {
    @Column(name = "assignment_id")
    private String assignmentId;

    @Column(name = "worker_id")
    private String workerId;
}
