package com.wastewise.routeservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ------------------------------------------------------------------------------
 * Route Entity
 * ------------------------------------------------------------------------------
 * Represents a waste pickup route associated with a specific zone.
 * Includes route metadata and auditing fields.
 * ------------------------------------------------------------------------------
 */
@Entity
@Table(name = "route")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route {

    /**
     * Unique Route ID (e.g., Z001-R001).
     */
    @Id
    @Column(name = "route_id", nullable = false, unique = true)
    private String routeId;

    /**
     * Name of the route (must be unique within a zone).
     */
    @Column(name = "route_name", nullable = false)
    private String routeName;

    /**
     * Zone ID to which this route belongs.
     */
    @Column(name = "zone_id", nullable = false)
    private String zoneId;

    /**
     * pickup points for this route.
     */
    
    @Column(name = "path_details", nullable = false)
    private String pickupPoints;


    /**
     * Estimated time for completing this route in minutes.
     */
    @Column(name = "estimated_time", nullable = false)
    private int estimatedTime;

    /**
     * Timestamp when the route was created.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the route was last updated.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
