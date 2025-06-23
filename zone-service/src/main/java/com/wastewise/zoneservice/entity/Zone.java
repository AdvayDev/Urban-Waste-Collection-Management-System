package com.wastewise.zoneservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entity representing a Zone.
 */
@Entity
@Table(name = "zone")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Zone {

    @Id
    @Column(name = "zone_id", nullable = false, unique = true)
    private String zoneId;

    @Column(name = "zone_name", nullable = false, unique = true)
    private String zoneName;

    @Column(name = "area_coverage", nullable = false)
    private Long areaCoverage;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
