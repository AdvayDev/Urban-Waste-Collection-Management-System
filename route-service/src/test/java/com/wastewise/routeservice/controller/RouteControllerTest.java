package com.wastewise.routeservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wastewise.routeservice.dto.RouteCreationRequestDTO;
import com.wastewise.routeservice.dto.RouteResponseDTO;
import com.wastewise.routeservice.dto.RouteUpdateRequestDTO;
import com.wastewise.routeservice.exception.GlobalExceptionHandler;
import com.wastewise.routeservice.exception.custom.NoRouteChangesDetectedException;
import com.wastewise.routeservice.exception.custom.RouteNotFoundException;
import com.wastewise.routeservice.exception.custom.ZoneNotFoundException;
import com.wastewise.routeservice.service.RouteService;

/**
 * ------------------------------------------------------------------------------
 * RouteControllerTest
 * ------------------------------------------------------------------------------
 * Unit tests for RouteController using @WebMvcTest
 * Simulates HTTP request/response lifecycle
 * Mocks RouteService and tests exception handling and response validation
 * ------------------------------------------------------------------------------
 */
@WebMvcTest(RouteController.class)
@ContextConfiguration(classes = {RouteController.class, GlobalExceptionHandler.class})
@Import(GlobalExceptionHandler.class)
public class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RouteService routeService;

    @Autowired
    private ObjectMapper objectMapper;

    private RouteResponseDTO mockRoute;

    @BeforeEach
    void setup() {
        mockRoute = RouteResponseDTO.builder()
                .routeId("Z001-R001")
                .routeName("RouteA")
                .zoneId("Z001")
                .pickupPoints("P1,P2")
                .estimatedTime(30)
                .build();
    }

    @Test
    void createRoute_success() throws Exception {
        RouteCreationRequestDTO request = new RouteCreationRequestDTO("Z001", "RouteA", "P1,P2", 30);
        Mockito.when(routeService.createRoute(any())).thenReturn(mockRoute);

        mockMvc.perform(post("/wastewise/admin/routes/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.routeId").value("Z001-R001"))
                .andExpect(jsonPath("$.message").value("New route created with ID")); // ✅ fixed here
    }

    @Test
    void createRoute_zoneNotFound_throwsException() throws Exception {
        Mockito.when(routeService.createRoute(any()))
                .thenThrow(new ZoneNotFoundException("Z001"));

        RouteCreationRequestDTO request = new RouteCreationRequestDTO("Z001", "RouteA", "P1,P2", 30);

        mockMvc.perform(post("/wastewise/admin/routes/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Zone with ID 'Z001' does not exist."));
    }

    @Test
    void updateRoute_success() throws Exception {
        RouteUpdateRequestDTO request = new RouteUpdateRequestDTO("RouteB", "P1,P2,P3", 40);
        Mockito.when(routeService.updateRoute(eq("Z001-R001"), any())).thenReturn(mockRoute);

        mockMvc.perform(put("/wastewise/admin/routes/update/Z001-R001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.routeId").value("Z001-R001"))
                .andExpect(jsonPath("$.message").value("Route updated successfully"));
    }

    @Test
    void updateRoute_noChanges_throwsException() throws Exception {
        RouteUpdateRequestDTO request = new RouteUpdateRequestDTO("RouteA", "P1,P2", 30);

        Mockito.when(routeService.updateRoute(eq("Z001-R001"), any()))
                .thenThrow(new NoRouteChangesDetectedException("Z001-R001"));

        mockMvc.perform(put("/wastewise/admin/routes/update/Z001-R001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("No changes detected for route with ID Z001-R001"));
    }

    @Test
    void getRouteById_success() throws Exception {
        Mockito.when(routeService.getRouteById("Z001-R001")).thenReturn(mockRoute);

        mockMvc.perform(get("/wastewise/admin/routes/Z001-R001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.routeId").value("Z001-R001"))
                .andExpect(jsonPath("$.message").value("Route retrieved successfully"));
    }

    @Test
    void getRouteById_notFound() throws Exception {
        Mockito.when(routeService.getRouteById("INVALID"))
                .thenThrow(new RouteNotFoundException("INVALID"));

        mockMvc.perform(get("/wastewise/admin/routes/INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Route with ID INVALID not found."));
    }

    @Test
    void getAllRoutes_success() throws Exception {
        Mockito.when(routeService.getAllRoutes()).thenReturn(List.of(mockRoute));

        mockMvc.perform(get("/wastewise/admin/routes/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].routeId").value("Z001-R001"))
                .andExpect(jsonPath("$.message").value("Routes retrieved successfully"));
    }

    @Test
    void deleteRoute_success() throws Exception {
        Mockito.doNothing().when(routeService).deleteRoute("Z001-R001");

        mockMvc.perform(delete("/wastewise/admin/routes/delete/Z001-R001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Route deleted successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getRoutesByZoneId_success() throws Exception {
        Mockito.when(routeService.getRouteIdsByZoneId("Z001"))
                .thenReturn(List.of("Z001-R001", "Z001-R002"));

        mockMvc.perform(get("/wastewise/admin/routes/zone/Z001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").value("Z001-R001"))
                .andExpect(jsonPath("$.data[1]").value("Z001-R002"))
                .andExpect(jsonPath("$.message").value("Routes retrieved successfully"));
    }
}
