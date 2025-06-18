package com.wastewise.worker.management.model;

import com.wastewise.worker.management.enums.Shift;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "worker_assignment")
public class WorkerAssignment {

    @EmbeddedId
    private WorkerAssignmentId id;

    @OneToOne
    @MapsId("workerId")  // maps the embedded FK
    @JoinColumn(name = "worker_id", referencedColumnName = "worker_id")
    private Worker worker;

    @Column(name = "zone_id")
    private String zoneId;

    @Column(name = "route_id")
    private String routeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "shift")
    private Shift shift;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}