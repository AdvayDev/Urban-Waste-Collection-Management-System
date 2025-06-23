package com.wastewise.zoneservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO containing only the zone ID and zone name.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZoneNameAndIdResponse {
    private String zoneId;
    private String zoneName;
}
