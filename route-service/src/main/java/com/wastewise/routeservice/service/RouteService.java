package com.wastewise.routeservice.service;

import com.wastewise.routeservice.dto.RouteCreationRequestDTO;
import com.wastewise.routeservice.dto.RouteUpdateRequestDTO;
import com.wastewise.routeservice.dto.RouteResponseDTO;

import java.util.List;

/**
 * ------------------------------------------------------------------------------
 * Interface for Route service operations.
 * ------------------------------------------------------------------------------
 */
public interface RouteService {

    /**
     * Create a new route.
     *
     * @param request the route creation request
     * @return the created route
     */
    RouteResponseDTO createRoute(RouteCreationRequestDTO request);

    /**
     * Update an existing route.
     *
     * @param routeId route ID
     * @param request updated data
     * @return updated route
     */
    RouteResponseDTO updateRoute(String routeId, RouteUpdateRequestDTO request);

    /**
     * Delete route by ID.
     *
     * @param routeId route ID
     */
    void deleteRoute(String routeId);

    /**
     * Get all routes.
     *
     * @return list of routes
     */
    List<RouteResponseDTO> getAllRoutes();

    /**
     * Get a route by its ID.
     *
     * @param routeId route ID
     * @return route data
     */
    RouteResponseDTO getRouteById(String routeId);

    /**
     * Get route IDs for a specific zone.
     *
     * @param zoneId zone ID
     * @return list of route IDs
     */
    List<String> getRouteIdsByZoneId(String zoneId);
}
