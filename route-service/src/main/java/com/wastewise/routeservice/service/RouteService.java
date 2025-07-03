package com.wastewise.routeservice.service;

import com.wastewise.routeservice.dto.RouteCreationRequestDTO;
import com.wastewise.routeservice.dto.RouteUpdateRequestDTO;
import com.wastewise.routeservice.dto.RouteResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Route management.
 */
public interface RouteService {

    RouteResponseDTO createRoute(RouteCreationRequestDTO request);

    RouteResponseDTO updateRoute(String routeId, RouteUpdateRequestDTO request);

    void deleteRoute(String routeId);

    List<RouteResponseDTO> getAllRoutes();

    Page<RouteResponseDTO> getAllRoutes(Pageable pageable); // ✅ ADDED

    RouteResponseDTO getRouteById(String routeId);

    List<String> getRouteIdsByZoneId(String zoneId);
}