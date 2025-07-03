package com.wastewise.routeservice.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import com.wastewise.routeservice.client.AssignmentClient;
import com.wastewise.routeservice.client.ZoneClient;
import com.wastewise.routeservice.dto.RouteCreationRequestDTO;
import com.wastewise.routeservice.dto.RouteResponseDTO;
import com.wastewise.routeservice.dto.AssignmentDTO;
import com.wastewise.routeservice.dto.RouteUpdateRequestDTO;
import com.wastewise.routeservice.entity.Route;
import com.wastewise.routeservice.exception.custom.DuplicateRouteNameException;
import com.wastewise.routeservice.exception.custom.NoRouteChangesDetectedException;
import com.wastewise.routeservice.exception.custom.RouteDeletionException;
import com.wastewise.routeservice.exception.custom.RouteNotFoundException;
import com.wastewise.routeservice.exception.custom.ZoneNotFoundException;
import com.wastewise.routeservice.payload.RestResponse;
import com.wastewise.routeservice.repository.RouteRepository;
import com.wastewise.routeservice.service.RouteService;
import com.wastewise.routeservice.util.RouteIdGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ------------------------------------------------------------------------------
 * Service Implementation: RouteServiceImpl
 * ------------------------------------------------------------------------------
 * Handles core business logic for route management:
 * - Validates zones via Feign client.
 * - Prevents duplicate routes in the same zone.
 * - Converts route entity to response DTO.
 * ------------------------------------------------------------------------------
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final RouteIdGenerator routeIdGenerator;
    private final ZoneClient zoneClient;
    private final AssignmentClient assignmentClient;

    /**
     * Creates a new route if the zone exists and name is unique in zone.
     *
     * @param requestDto Route creation request DTO
     * @return RouteResponse
     */
    @Override
    public RouteResponseDTO createRoute(RouteCreationRequestDTO requestDto) {
        log.info("Creating route for zone: {}", requestDto.getZoneId());

        RestResponse<Boolean> zoneResponse = zoneClient.existsByZoneId(requestDto.getZoneId());
        if (zoneResponse.getData() == null || !zoneResponse.getData()) {
            throw new ZoneNotFoundException(requestDto.getZoneId());
        }

        routeRepository.findByRouteNameAndZoneId(requestDto.getRouteName(), requestDto.getZoneId())
                .ifPresent(route -> {
                    throw new DuplicateRouteNameException(requestDto.getRouteName(), requestDto.getZoneId());
                });

        String routeId = routeIdGenerator.generateRouteId(requestDto.getZoneId());

        Route route = Route.builder()
                .routeId(routeId)
                .routeName(requestDto.getRouteName())
                .pickupPoints(requestDto.getPickupPoints())
                .zoneId(requestDto.getZoneId())
                .estimatedTime(requestDto.getEstimatedTime())
                .build();

        routeRepository.save(route);
        log.info("Route created successfully with ID: {}", routeId);
        return mapToResponse(route);
    }

    @Override
    public RouteResponseDTO updateRoute(String routeId, RouteUpdateRequestDTO requestDto) {
        log.info("Updating route with ID: {}", routeId);

        Route existingRoute = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException(routeId));

        boolean isNameChanged = !existingRoute.getRouteName().equalsIgnoreCase(requestDto.getRouteName());
        boolean isPointsChanged = !existingRoute.getPickupPoints().equals(requestDto.getPickupPoints());
        boolean isTimeChanged = existingRoute.getEstimatedTime() != requestDto.getEstimatedTime();

        if (!isNameChanged && !isPointsChanged && !isTimeChanged) {
            throw new NoRouteChangesDetectedException(routeId);
        }

        if (isNameChanged &&
                routeRepository.findByRouteNameAndZoneId(requestDto.getRouteName(), existingRoute.getZoneId()).isPresent()) {
            throw new DuplicateRouteNameException(requestDto.getRouteName(), existingRoute.getZoneId());
        }

        existingRoute.setRouteName(requestDto.getRouteName());
        existingRoute.setPickupPoints(requestDto.getPickupPoints());
        existingRoute.setEstimatedTime(requestDto.getEstimatedTime());

        routeRepository.save(existingRoute);
        log.info("Route updated successfully for ID: {}", routeId);
        return mapToResponse(existingRoute);
    }

    @Override
    public void deleteRoute(String routeId) {
        log.info("Deleting route with ID: {}", routeId);

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException(routeId));

        List<AssignmentDTO> assignments = assignmentClient.getAssignmentsByRouteId(routeId);
        List<String> assignmentIds = assignments.stream()
                .map(AssignmentDTO::getId)
                .collect(Collectors.toList());

        if (!assignmentIds.isEmpty()) {
            log.error("Cannot delete route {}: assignments found: {}", routeId, assignmentIds);
            throw new RouteDeletionException(routeId, assignmentIds);
        }

        routeRepository.delete(route);
        log.info("Route deleted successfully: {}", routeId);
    }



    @Override
    public List<RouteResponseDTO> getAllRoutes() {
        log.info("Fetching all routes");
        return routeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RouteResponseDTO getRouteById(String routeId) {
        log.info("Fetching route with ID: {}", routeId);
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException(routeId));
        return mapToResponse(route);
    }

    @Override
    public List<String> getRouteIdsByZoneId(String zoneId) {
        log.info("Fetching route IDs for zone ID: {}", zoneId);
        return routeRepository.findByZoneId(zoneId)
                .stream()
                .map(Route::getRouteId)
                .collect(Collectors.toList());
    }
    @Override
    public Page<RouteResponseDTO> getAllRoutes(Pageable pageable) {
        log.info("Fetching paginated routes with {}", pageable);
        return routeRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    private RouteResponseDTO mapToResponse(Route route) {
        return RouteResponseDTO.builder()
                .routeId(route.getRouteId())
                .routeName(route.getRouteName())
                .zoneId(route.getZoneId())
                .pickupPoints(route.getPickupPoints())
                .estimatedTime(route.getEstimatedTime())
                .build();
    }
}
