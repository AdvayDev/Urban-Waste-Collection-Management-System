package com.wastewise.routeservice.dto;

import lombok.*;

/**
 * ------------------------------------------------------------------------------
 * DTO: RouteCreationRequest
 * ------------------------------------------------------------------------------
 * Represents the request body for creating a new route.
 * ------------------------------------------------------------------------------
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteCreationRequestDTO {

    /**
     * Zone ID to which this route belongs (e.g., Z001).
     */
    private String zoneId;

    /**
     * Name of the route.
     */
    private String routeName;

    /**
     * Pickup points represented as a comma-separated string (e.g., "Point1,Point2,Point3").
     */
    private String pickupPoints;

    /**
     * Estimated time to complete the route in minutes.
     */
    private int estimatedTime;
}
