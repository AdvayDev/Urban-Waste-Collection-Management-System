package com.wastewise.routeservice.dto;

import lombok.*;

/**
 * ------------------------------------------------------------------------------
 * DTO: RouteUpdateRequest
 * ------------------------------------------------------------------------------
 * Represents the request body for updating an existing route.
 * ------------------------------------------------------------------------------
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteUpdateRequestDTO {

    /**
     * New name for the route.
     */
    private String routeName;

    /**
     * Updated pickup points as a comma-separated string (e.g., "PointA,PointB").
     */
    private String pickupPoints;

    /**
     * Updated estimated time for completing the route in minutes.
     */
    private Integer estimatedTime;
}
