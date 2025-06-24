package com.wastewise.worker.management.model;

import com.wastewise.worker.management.enums.WorkerStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "worker")
public class Worker {
    @Id
    @Column(name = "worker_id")
    private String workerId;

    @Column(name = "name")
    private String name;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "worker_status")
    @Enumerated(EnumType.STRING)
    private WorkerStatus workerStatus;

    @Column(name = "role_id")
    private String roleId;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

}
