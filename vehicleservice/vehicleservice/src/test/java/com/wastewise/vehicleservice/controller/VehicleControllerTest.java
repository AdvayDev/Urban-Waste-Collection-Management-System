package com.wastewise.vehicleservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wastewise.vehicleservice.config.TestSecurityConfig;
import com.wastewise.vehicleservice.dto.VehicleDTO;
import com.wastewise.vehicleservice.enums.VehicleStatus;
import com.wastewise.vehicleservice.enums.VehicleType;
import com.wastewise.vehicleservice.service.VehicleService;

@WebMvcTest(VehicleController.class)
@Import(TestSecurityConfig.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleService vehicleService;

    @Autowired
    private ObjectMapper objectMapper;

    private VehicleDTO dto;

    @BeforeEach
    void setup() {
        dto = VehicleDTO.builder()
                .vehicleId("PT001")
                .registrationNo("AP16AB1234")
                .type(VehicleType.PICKUP_TRUCK)
                .status(VehicleStatus.AVAILABLE)
                .build();
    }

    @Test
    void testCreateVehicle() throws Exception {
        when(vehicleService.createVehicle(any())).thenReturn(dto);

        mockMvc.perform(post("/wastewise/admin/vehicle-management")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vehicleId").value("PT001"));
    }

    @Test
    void testGetVehicleById() throws Exception {
        when(vehicleService.getVehicleById("PT001")).thenReturn(dto);

        mockMvc.perform(get("/wastewise/admin/vehicle-management/PT001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registrationNo").value("AP16AB1234"));
    }

    @Test
    void testGetAllVehicles() throws Exception {
        when(vehicleService.getAllVehicles()).thenReturn(List.of(dto));

        mockMvc.perform(get("/wastewise/admin/vehicle-management"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testUpdateVehicle() throws Exception {
        doNothing().when(vehicleService).updateVehicle(eq("PT001"), any());

        mockMvc.perform(put("/wastewise/admin/vehicle-management/PT001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteVehicle() throws Exception {
        doNothing().when(vehicleService).deleteVehicle("PT001");

        mockMvc.perform(delete("/wastewise/admin/vehicle-management/PT001"))
                .andExpect(status().isOk());
    }

    @Test
    void testFilterPickupTruck() throws Exception {
        when(vehicleService.getVehiclesByTypeAndStatus("PICKUP_TRUCK", "AVAILABLE"))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/wastewise/admin/vehicle-management/filter/pickuptruck"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
    @Test
    void testFilterRouteTruck() throws Exception {
        when(vehicleService.getVehiclesByTypeAndStatus("ROUTE_TRUCK", "AVAILABLE"))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/wastewise/admin/vehicle-management/filter/routetruck"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
