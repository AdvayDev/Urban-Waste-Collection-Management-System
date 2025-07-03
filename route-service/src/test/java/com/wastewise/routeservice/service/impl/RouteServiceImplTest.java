package com.wastewise.routeservice.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.wastewise.routeservice.client.AssignmentClient;
import com.wastewise.routeservice.client.ZoneClient;
import com.wastewise.routeservice.dto.RouteCreationRequestDTO;
import com.wastewise.routeservice.dto.RouteUpdateRequestDTO;
import com.wastewise.routeservice.entity.Route;
import com.wastewise.routeservice.exception.custom.DuplicateRouteNameException;
import com.wastewise.routeservice.exception.custom.NoRouteChangesDetectedException;
import com.wastewise.routeservice.exception.custom.RouteDeletionException;
import com.wastewise.routeservice.exception.custom.RouteNotFoundException;
import com.wastewise.routeservice.exception.custom.ZoneNotFoundException;
import com.wastewise.routeservice.payload.RestResponse;
import com.wastewise.routeservice.repository.RouteRepository;
import com.wastewise.routeservice.util.RouteIdGenerator;

/**
 * ------------------------------------------------------------------------------
 * RouteServiceImplTest
 * ------------------------------------------------------------------------------
 * Unit tests for RouteServiceImpl focusing on:
 * - Business logic
 * - Custom exception coverage
 * - Mocked external dependencies (ZoneClient)
 * ------------------------------------------------------------------------------
 */
class RouteServiceImplTest {

    @Mock private RouteRepository routeRepository;
    @Mock private RouteIdGenerator routeIdGenerator;
    @Mock private ZoneClient zoneClient;
    @Mock private AssignmentClient assignmentClient;

    @InjectMocks private RouteServiceImpl routeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * ✅ Create route successfully.
     */
    @Test
    void createRoute_success() {
        RouteCreationRequestDTO request = new RouteCreationRequestDTO("Z001", "RouteA", "P1,P2", 30);

        when(zoneClient.existsByZoneId("Z001"))
                .thenReturn(RestResponse.<Boolean>builder().data(true).message("OK").build());
        when(routeRepository.findByRouteNameAndZoneId("RouteA", "Z001")).thenReturn(Optional.empty());
        when(routeIdGenerator.generateRouteId("Z001")).thenReturn("Z001-R001");

        Route saved = Route.builder()
                .routeId("Z001-R001")
                .routeName("RouteA")
                .pickupPoints("P1,P2")
                .zoneId("Z001")
                .estimatedTime(30)
                .build();

        when(routeRepository.save(any())).thenReturn(saved);

        var response = routeService.createRoute(request);

        assertThat(response.getRouteId()).isEqualTo("Z001-R001");
        assertThat(response.getPickupPoints()).isEqualTo("P1,P2");
    }

    /**
     * ❌ Create route with invalid zone.
     */
    @Test
    void createRoute_invalidZone_throwsException() {
        RouteCreationRequestDTO request = new RouteCreationRequestDTO("Z999", "RouteA", "P1", 25);

        when(zoneClient.existsByZoneId("Z999"))
                .thenReturn(RestResponse.<Boolean>builder().data(false).message("Zone not found").build());

        assertThatThrownBy(() -> routeService.createRoute(request))
                .isInstanceOf(ZoneNotFoundException.class)
                .hasMessageContaining("Z999");
    }

    /**
     * ❌ Create route with duplicate name in same zone.
     */
    @Test
    void createRoute_duplicateName_throwsException() {
        RouteCreationRequestDTO request = new RouteCreationRequestDTO("Z001", "RouteA", "P1", 20);

        when(zoneClient.existsByZoneId("Z001"))
                .thenReturn(RestResponse.<Boolean>builder().data(true).build());
        when(routeRepository.findByRouteNameAndZoneId("RouteA", "Z001"))
                .thenReturn(Optional.of(new Route()));

        assertThatThrownBy(() -> routeService.createRoute(request))
                .isInstanceOf(DuplicateRouteNameException.class)
                .hasMessageContaining("RouteA");
    }

    /**
     * ✅ Update route successfully.
     */
    @Test
    void updateRoute_success() {
        RouteUpdateRequestDTO request = new RouteUpdateRequestDTO("RouteB", "P3", 40);

        Route existing = Route.builder()
                .routeId("Z001-R001")
                .routeName("RouteA")
                .pickupPoints("P1")
                .zoneId("Z001")
                .estimatedTime(30)
                .build();

        when(routeRepository.findById("Z001-R001")).thenReturn(Optional.of(existing));
        when(routeRepository.findByRouteNameAndZoneId("RouteB", "Z001")).thenReturn(Optional.empty());
        when(routeRepository.save(any())).thenReturn(existing);

        var updated = routeService.updateRoute("Z001-R001", request);

        assertThat(updated.getRouteName()).isEqualTo("RouteB");
    }

    /**
     * ❌ Update route with no changes.
     */
    @Test
    void updateRoute_noChanges_throwsException() {
        RouteUpdateRequestDTO request = new RouteUpdateRequestDTO("RouteA", "P1", 30);
        Route existing = new Route("Z001-R001", "RouteA", "Z001", "P1", 30, null, null);

        when(routeRepository.findById("Z001-R001")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> routeService.updateRoute("Z001-R001", request))
                .isInstanceOf(NoRouteChangesDetectedException.class);
    }

    /**
     * ❌ Update route with duplicate name in zone.
     */
    @Test
    void updateRoute_duplicateName_throwsException() {
        RouteUpdateRequestDTO request = new RouteUpdateRequestDTO("RouteX", "P1", 35);

        Route existing = Route.builder()
                .routeId("Z001-R001")
                .routeName("RouteA")
                .zoneId("Z001")
                .pickupPoints("P1")
                .estimatedTime(30)
                .build();

        when(routeRepository.findById("Z001-R001")).thenReturn(Optional.of(existing));
        when(routeRepository.findByRouteNameAndZoneId("RouteX", "Z001"))
                .thenReturn(Optional.of(new Route()));

        assertThatThrownBy(() -> routeService.updateRoute("Z001-R001", request))
                .isInstanceOf(DuplicateRouteNameException.class);
    }

    /**
     * ❌ Update route not found.
     */
    @Test
    void updateRoute_notFound_throwsException() {
        when(routeRepository.findById("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> routeService.updateRoute("INVALID", new RouteUpdateRequestDTO("RouteX", "P1", 20)))
                .isInstanceOf(RouteNotFoundException.class);
    }

    /**
     * ✅ Delete route successfully.
     */
    @Test
    void deleteRoute_success() {
        Route route = new Route("Z001-R001", "RouteA", "Z001", "P1", 25, null, null);

        when(routeRepository.findById("Z001-R001")).thenReturn(Optional.of(route));

        // ✅ Mock the assignmentClient to return an empty list inside a RestResponse
        RestResponse<List<String>> mockResponse = RestResponse.<List<String>>builder()
                .message("No assignments")
                .data(List.of())
                .build();

        when(assignmentClient.getAssignmentIdsByRouteId("Z001-R001")).thenReturn(mockResponse);

        routeService.deleteRoute("Z001-R001");

        verify(routeRepository).delete(route);
    }

    /**
     * ❌ Delete route not found.
     */
    @Test
    void deleteRoute_notFound_throwsException() {
        when(routeRepository.findById("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> routeService.deleteRoute("INVALID"))
                .isInstanceOf(RouteNotFoundException.class);
    }

    /**
     * ✅ Get route by ID.
     */
    @Test
    void getRouteById_success() {
        Route route = new Route("Z001-R001", "RouteA", "Z001", "P1", 25, null, null);
        when(routeRepository.findById("Z001-R001")).thenReturn(Optional.of(route));

        var result = routeService.getRouteById("Z001-R001");

        assertThat(result.getRouteName()).isEqualTo("RouteA");
    }

    /**
     * ❌ Get route by invalid ID.
     */
    @Test
    void getRouteById_notFound_throwsException() {
        when(routeRepository.findById("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> routeService.getRouteById("INVALID"))
                .isInstanceOf(RouteNotFoundException.class);
    }

    /**
     * ✅ Get all routes.
     */
    @Test
    void getAllRoutes_success() {
        when(routeRepository.findAll()).thenReturn(List.of(
                new Route("Z001-R001", "RouteX", "Z001", "P1", 15, null, null)
        ));

        var result = routeService.getAllRoutes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRouteName()).isEqualTo("RouteX");
    }

    /**
     * ✅ Get all route IDs for zone.
     */
    @Test
    void getRouteIdsByZoneId_success() {
        when(routeRepository.findByZoneId("Z001")).thenReturn(List.of(
                new Route("Z001-R001", "R1", "Z001", "P1", 30, null, null),
                new Route("Z001-R002", "R2", "Z001", "P2", 20, null, null)
        ));

        List<String> routeIds = routeService.getRouteIdsByZoneId("Z001");

        assertThat(routeIds).containsExactly("Z001-R001", "Z001-R002");
    }
    
    @Test
    void deleteRoute_success_whenNoAssignments() {
        Route route = new Route("Z001-R001", "RouteA", "Z001", "P1,P2", 25, null, null);
        when(routeRepository.findById("Z001-R001")).thenReturn(Optional.of(route));
        when(assignmentClient.getAssignmentIdsByRouteId("Z001-R001"))
                .thenReturn(RestResponse.<List<String>>builder().data(Collections.emptyList()).build());

        routeService.deleteRoute("Z001-R001");

        verify(routeRepository).delete(route);
    }
    
    @Test
    void deleteRoute_failure_dueToAssignments() {
        Route route = new Route("Z001-R001", "RouteA", "Z001", "P1,P2", 25, null, null);
        List<String> assignments = List.of("A001", "A002");

        when(routeRepository.findById("Z001-R001")).thenReturn(Optional.of(route));
        when(assignmentClient.getAssignmentIdsByRouteId("Z001-R001"))
                .thenReturn(RestResponse.<List<String>>builder().data(assignments).build());

        assertThatThrownBy(() -> routeService.deleteRoute("Z001-R001"))
                .isInstanceOf(RouteDeletionException.class)
                .hasMessageContaining("Z001-R001")
                .hasMessageContaining("A001");

        verify(routeRepository, never()).delete(any());
    }
}
