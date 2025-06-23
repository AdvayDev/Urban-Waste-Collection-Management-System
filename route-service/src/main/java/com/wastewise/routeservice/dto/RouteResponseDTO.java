package com.wastewise.routeservice.dto;

import lombok.*;

/**
 * ------------------------------------------------------------------------------
 * DTO: RouteResponse
 * ------------------------------------------------------------------------------
 * Represents the response body returned for all route operations.
 * ------------------------------------------------------------------------------
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponseDTO {

    /**
     * Unique route identifier (e.g., Z001-R001).
     */
    private String routeId;

    /**
     * Zone ID this route belongs to.
     */
    private String zoneId;

    /**
     * Name of the route.
     */
    private String routeName;

    /**
     * Estimated time (in minutes) to complete the route.
     */
    private int estimatedTime;

    /**
     * Pickup points stored as a comma-separated string.
     */
    private String pickupPoints;
}
