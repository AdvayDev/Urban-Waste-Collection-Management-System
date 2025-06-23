package com.wastewise.zoneservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for zone response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneResponseDTO {

    private String zoneId;
    private String zoneName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
