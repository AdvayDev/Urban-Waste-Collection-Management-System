package com.wastewise.vehicleservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wastewise.vehicleservice.controller.VehicleController;
import com.wastewise.vehicleservice.dto.VehicleDTO;
import com.wastewise.vehicleservice.enums.VehicleStatus;
import com.wastewise.vehicleservice.enums.VehicleType;
import com.wastewise.vehicleservice.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleService vehicleService;

    @Autowired
    private ObjectMapper objectMapper;

    private VehicleDTO validDto;

    @BeforeEach
    void setUp() {
        validDto = VehicleDTO.builder()
                .registrationNo("TS01AB1234")
                .type(VehicleType.PICKUP_TRUCK)
                .status(VehicleStatus.AVAILABLE)
                .build();
    }

    @Test
    void testResourceNotFoundException() throws Exception {
        when(vehicleService.getVehicleById("INVALID_ID"))
                .thenThrow(new ResourceNotFoundException("Vehicle not found with ID: INVALID_ID"));

        mockMvc.perform(get("/wastewise/admin/vehicle-management/INVALID_ID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Vehicle not found with ID: INVALID_ID"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void testDataIntegrityViolationException() throws Exception {
        when(vehicleService.createVehicle(any()))
                .thenThrow(new DataIntegrityViolationException("Duplicate registration"));

        mockMvc.perform(post("/wastewise/admin/vehicle-management")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Vehicle with this registration number already exists."))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void testIllegalArgumentException() throws Exception {
        when(vehicleService.getVehicleById("BAD_ID"))
                .thenThrow(new IllegalArgumentException("Invalid vehicle ID format"));

        mockMvc.perform(get("/wastewise/admin/vehicle-management/BAD_ID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid vehicle ID format"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testHttpMessageNotReadableException() throws Exception {
        String invalidJson = """
                {
                  "registrationNo": "TS09ZZ0001",
                  "type": "INVALID_TYPE",
                  "status": "AVAILABLE"
                }
                """;

        mockMvc.perform(post("/wastewise/admin/vehicle-management")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request format or enum value. Please check vehicle type/status and JSON structure."))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testValidationException() throws Exception {
        VehicleDTO invalidDto = VehicleDTO.builder()
                .registrationNo("")  // invalid: blank
                .type(VehicleType.PICKUP_TRUCK)
                .status(VehicleStatus.AVAILABLE)
                .build();

        mockMvc.perform(post("/wastewise/admin/vehicle-management")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testUnhandledGenericException() throws Exception {
        when(vehicleService.getVehicleById("ANY_ID"))
                .thenThrow(new RuntimeException("Unexpected server error"));

        mockMvc.perform(get("/wastewise/admin/vehicle-management/ANY_ID"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please contact support."))
                .andExpect(jsonPath("$.status").value(500));
    }
}
