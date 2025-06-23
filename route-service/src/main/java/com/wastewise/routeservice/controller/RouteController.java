package com.wastewise.routeservice.controller;

import com.wastewise.routeservice.constant.RouteConstants;
import com.wastewise.routeservice.dto.RouteCreationRequestDTO;
import com.wastewise.routeservice.dto.RouteUpdateRequestDTO;
import com.wastewise.routeservice.dto.RouteResponseDTO;
import com.wastewise.routeservice.payload.RestResponse;
import com.wastewise.routeservice.service.RouteService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing route APIs.
 */
@RestController
@RequestMapping(RouteConstants.BASE_ROUTE_API)
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;
    private static final Logger logger = LoggerFactory.getLogger(RouteController.class);

    /**
     * Create a new route.
     *
     * @param request Route creation data
     * @return Response with created route ID
     */
    @PostMapping("/create")
    public ResponseEntity<RestResponse<Object>> createRoute(@Valid @RequestBody RouteCreationRequestDTO request) {
        logger.info("Received request to create route for zone: {}", request.getZoneId());
        RouteResponseDTO created = routeService.createRoute(request);
        logger.info("Route created successfully with ID: {}", created.getRouteId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                RestResponse.builder()
                        .message(RouteConstants.ROUTE_CREATED_MSG)
                        .data(Map.of("routeId", created.getRouteId()))
                        .build()
        );
    }

    /**
     * Update an existing route.
     *
     * @param routeId Route ID to update
     * @param request Updated route data
     * @return Response with updated route details
     */
    @PutMapping("/update/{routeId}")
    public ResponseEntity<RestResponse<Object>> updateRoute(@PathVariable String routeId,
                                                            @Valid @RequestBody RouteUpdateRequestDTO request) {
        logger.info("Received request to update route with ID: {}", routeId);
        RouteResponseDTO updated = routeService.updateRoute(routeId, request);
        logger.info("Route updated successfully with ID: {}", updated.getRouteId());
        return ResponseEntity.ok(
                RestResponse.builder()
                        .message(RouteConstants.ROUTE_UPDATED_MSG)
                        .data(Map.of(
                                "routeId", updated.getRouteId(),
                                "routeName", updated.getRouteName(),
                                "zoneId", updated.getZoneId(),
                                "pickupPoints", updated.getPickupPoints(),
                                "estimatedTime", updated.getEstimatedTime()
                        ))
                        .build()
        );
    }

    /**
     * Delete a route by ID.
     *
     * @param routeId ID of route to delete
     * @return Response message
     */
    @DeleteMapping("/delete/{routeId}")
    public ResponseEntity<RestResponse<Object>> deleteRoute(@PathVariable String routeId) {
        logger.info("Received request to delete route with ID: {}", routeId);
        routeService.deleteRoute(routeId);
        logger.info("Route deleted successfully with ID: {}", routeId);
        return ResponseEntity.ok(
                RestResponse.builder()
                        .message(RouteConstants.ROUTE_DELETED_MSG)
                        .data(null)
                        .build()
        );
    }

    /**
     * Get list of all routes.
     *
     * @return List of route responses
     */
    @GetMapping("/list")
    public ResponseEntity<RestResponse<Object>> getAllRoutes() {
        logger.info("Received request to fetch all routes");
        List<RouteResponseDTO> routes = routeService.getAllRoutes();
        logger.info("Returning {} routes", routes.size());
        return ResponseEntity.ok(
                RestResponse.builder()
                        .message(RouteConstants.ROUTES_LISTED_MSG)
                        .data(routes)
                        .build()
        );
    }

    /**
     * Get a route by ID.
     *
     * @param routeId Route ID
     * @return Route response
     */
    @GetMapping("/{routeId}")
    public ResponseEntity<RestResponse<Object>> getRouteById(@PathVariable String routeId) {
        logger.info("Received request to fetch route with ID: {}", routeId);
        RouteResponseDTO route = routeService.getRouteById(routeId);
        logger.info("Returning route with ID: {}", routeId);
        return ResponseEntity.ok(
                RestResponse.builder()
                        .message(RouteConstants.ROUTE_RETRIEVED_MSG)
                        .data(route)
                        .build()
        );
    }

    /**
     * Get list of route IDs for a specific zone.
     *
     * @param zoneId Zone ID
     * @return List of route IDs
     */
    @GetMapping("/zone/{zoneId}")
    public ResponseEntity<RestResponse<Object>> getRoutesByZoneId(@PathVariable String zoneId) {
        logger.info("Received request to fetch route IDs for zone: {}", zoneId);
        List<String> routeIds = routeService.getRouteIdsByZoneId(zoneId);
        logger.info("Returning {} route IDs for zone {}", routeIds.size(), zoneId);
        return ResponseEntity.ok(
            RestResponse.builder()
                .message("Routes retrieved successfully")
                .data(routeIds)
                .build()
        );
    }
}
