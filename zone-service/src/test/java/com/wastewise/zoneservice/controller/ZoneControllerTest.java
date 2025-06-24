package com.wastewise.zoneservice.controller;

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
import com.wastewise.zoneservice.dto.ZoneCreationRequestDTO;
import com.wastewise.zoneservice.dto.ZoneNameAndIdResponse;
import com.wastewise.zoneservice.dto.ZoneUpdateRequestDTO;
import com.wastewise.zoneservice.entity.Zone;
import com.wastewise.zoneservice.exception.GlobalExceptionHandler;
import com.wastewise.zoneservice.exception.custom.DuplicateZoneNameException;
import com.wastewise.zoneservice.exception.custom.NoZoneChangesDetectedException;
import com.wastewise.zoneservice.exception.custom.ZoneDeletionException;
import com.wastewise.zoneservice.exception.custom.ZoneNotFoundException;
import com.wastewise.zoneservice.service.ZoneService;

/**
 * ------------------------------------------------------------------------------
 * Unit tests for ZoneController using @WebMvcTest and MockMvc.
 * ------------------------------------------------------------------------------
 */
@WebMvcTest(ZoneController.class)
@ContextConfiguration(classes = {ZoneController.class, GlobalExceptionHandler.class})
@Import(GlobalExceptionHandler.class)
public class ZoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    
    @MockitoBean
    private ZoneService zoneService;

    private Zone mockZone;

    @BeforeEach
    void setup() {
        mockZone = Zone.builder()
                .zoneId("Z001")
                .zoneName("ZoneA")
                .areaCoverage(100L)
                .build();
    }

    @Test
    void createZone_success() throws Exception {
        ZoneCreationRequestDTO request = new ZoneCreationRequestDTO("ZoneA", 100L);
        Mockito.when(zoneService.createZone(any())).thenReturn(mockZone);

        mockMvc.perform(post("/wastewise/admin/zones/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("New zone created with ID"))
                .andExpect(jsonPath("$.data.zoneId").value("Z001"));
    }

    @Test
    void createZone_duplicate_throwsConflict() throws Exception {
        Mockito.when(zoneService.createZone(any()))
                .thenThrow(new DuplicateZoneNameException("ZoneA"));

        mockMvc.perform(post("/wastewise/admin/zones/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ZoneCreationRequestDTO("ZoneA", 100L))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Zone with name 'ZoneA' already exists."));
    }

    @Test
    void updateZone_noChanges_throwsConflict() throws Exception {
        Mockito.when(zoneService.updateZone(eq("Z001"), any()))
                .thenThrow(new NoZoneChangesDetectedException("Z001"));

        mockMvc.perform(put("/wastewise/admin/zones/update/Z001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ZoneUpdateRequestDTO("ZoneA", 100L))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("No changes in the zone details detected for zone ID Z001"));
    }

    @Test
    void updateZone_success() throws Exception {
        ZoneUpdateRequestDTO request = new ZoneUpdateRequestDTO("ZoneB", 200L);
        Zone updatedZone = Zone.builder()
                .zoneId("Z001")
                .zoneName("ZoneB")
                .areaCoverage(200L)
                .build();

        Mockito.when(zoneService.updateZone(eq("Z001"), any())).thenReturn(updatedZone);

        mockMvc.perform(put("/wastewise/admin/zones/update/Z001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Zone updated successfully"))
                .andExpect(jsonPath("$.data.zoneId").value("Z001"))
                .andExpect(jsonPath("$.data.zoneName").value("ZoneB"))
                .andExpect(jsonPath("$.data.areaCoverage").value(200));
    }

    @Test
    void getZoneById_success() throws Exception {
        Mockito.when(zoneService.getZoneById("Z001")).thenReturn(mockZone);

        mockMvc.perform(get("/wastewise/admin/zones/Z001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Zone retrieved successfully"))
                .andExpect(jsonPath("$.data.zoneName").value("ZoneA"));
    }

    @Test
    void getZoneById_notFound() throws Exception {
        Mockito.when(zoneService.getZoneById("Z999"))
                .thenThrow(new ZoneNotFoundException("Z999"));

        mockMvc.perform(get("/wastewise/admin/zones/Z999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Zone with ID Z999 not found."));
    }

    @Test
    void deleteZone_withRoutes_throwsBadRequest() throws Exception {
        Mockito.doThrow(new ZoneDeletionException("Z001", List.of("R001", "R002")))
                .when(zoneService).deleteZone("Z001");

        mockMvc.perform(delete("/wastewise/admin/zones/delete/Z001"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot delete zone Z001 because it has assigned routes: [R001, R002]"));
    }

    @Test
    void deleteZone_success() throws Exception {
        Mockito.doNothing().when(zoneService).deleteZone("Z001");

        mockMvc.perform(delete("/wastewise/admin/zones/delete/Z001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Zone deleted successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void zoneExists_returnsTrue() throws Exception {
        Mockito.when(zoneService.existsByZoneId("Z001")).thenReturn(true);

        mockMvc.perform(get("/wastewise/admin/zones/Z001/exists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Zone existence check completed"))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void getAllZones_success() throws Exception {
        Mockito.when(zoneService.getAllZones()).thenReturn(List.of(mockZone));

        mockMvc.perform(get("/wastewise/admin/zones/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Zones retrieved successfully"))
                .andExpect(jsonPath("$.data[0].zoneId").value("Z001"));
    }
    /**
     * Test: getAllZoneIdsAndNames returns only zoneId and zoneName.
     */
    @Test
    void getAllZoneIdsAndNames_success() throws Exception {
        var response = new ZoneNameAndIdResponse("Z001", "ZoneA");

        Mockito.when(zoneService.getAllZoneNamesAndIds())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/wastewise/admin/zones/namesandids"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].zoneId").value("Z001"))
                .andExpect(jsonPath("$.data[0].zoneName").value("ZoneA"))
                .andExpect(jsonPath("$.message").value("Zone names and IDs retrieved successfully"));
    }
	
}
