package com.WasteWise.WasteCollectionLogs.Controller;

import com.WasteWise.WasteCollectionLogs.Constants.WasteLogConstants;
import com.WasteWise.WasteCollectionLogs.Dto.VehicleReportDTO;
import com.WasteWise.WasteCollectionLogs.Dto.WasteLogResponseDTO;
import com.WasteWise.WasteCollectionLogs.Dto.WasteLogStartRequestDTO;
import com.WasteWise.WasteCollectionLogs.Dto.WasteLogUpdateRequestDTO;
import com.WasteWise.WasteCollectionLogs.Dto.ZoneReportDTO;
import com.WasteWise.WasteCollectionLogs.Handler.GlobalExceptionHandler;
import com.WasteWise.WasteCollectionLogs.Handler.InvalidInputException;
// import com.WasteWise.WasteCollectionLogs.Handler.LogAlreadyCompletedException; // Removed, as requested
import com.WasteWise.WasteCollectionLogs.Handler.ResourceNotFoundException;
import com.WasteWise.WasteCollectionLogs.ServiceImpl.WasteLogServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString; 

@WebMvcTest
@ContextConfiguration(classes = {WasteLogController.class})
@Import(GlobalExceptionHandler.class)
class WasteLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WasteLogServiceImpl wasteLogService;

    @Autowired
    private ObjectMapper objectMapper;

    private WasteLogStartRequestDTO startRequestDTO;
    private WasteLogUpdateRequestDTO updateRequestDTO;
    private WasteLogResponseDTO successStartResponseDTO;
    private WasteLogResponseDTO successEndResponseDTO;

    @BeforeEach
    void setUp() {
        startRequestDTO = new WasteLogStartRequestDTO("Z001", "RT001", "W001");
        
        updateRequestDTO = new WasteLogUpdateRequestDTO("W001", 150.0); 

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.minusHours(1);

        successStartResponseDTO = new WasteLogResponseDTO(
            123L,                       // logId
            "Z001",                     // zoneId
            "RT001",                    // vehicleId
            "W001",                     // workerId
            startTime,                  // collectionStartTime
            null,                       // collectionEndTime
            0.0,                        // weightCollected
            WasteLogConstants.WASTE_COLLECTION_LOG_RECORDED_SUCCESSFULLY, // message
            now.minusMinutes(70),       // createdDate
            now.minusMinutes(70)        // updatedDate
        );

        successEndResponseDTO = new WasteLogResponseDTO(
            123L,                       // logId
            "Z001",                     // zoneId
            "RT001",                    // vehicleId
            "W001",                     // workerId
            startTime,                  // collectionStartTime
            now,                        // collectionEndTime
            150.0,                      // weightCollected
            WasteLogConstants.WASTE_COLLECTION_LOG_COMPLETED_SUCCESSFULLY, // message
            now.minusMinutes(70),       // createdDate
            now                         // updatedDate
        );
    }

    // --- startCollection Tests ---

    @Test
    void startCollection_Success() throws Exception {
        when(wasteLogService.startCollection(any(WasteLogStartRequestDTO.class))).thenReturn(successStartResponseDTO);

        mockMvc.perform(post("/wastewise/admin/wastelogs/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(startRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(WasteLogConstants.WASTE_COLLECTION_LOG_RECORDED_SUCCESSFULLY))
                .andExpect(jsonPath("$.data.logId").value(123L)) 
                .andExpect(jsonPath("$.data.zoneId").value("Z001"))
                .andExpect(jsonPath("$.data.vehicleId").value("RT001"))
                .andExpect(jsonPath("$.data.workerId").value("W001"))
                .andExpect(jsonPath("$.data.collectionStartTime").exists()) 
                .andExpect(jsonPath("$.data.collectionEndTime").doesNotExist()) 
                .andExpect(jsonPath("$.data.weightCollected").value(0.0)); 
    }

    @Test
    void startCollection_InvalidInput_MissingFields() throws Exception {
        WasteLogStartRequestDTO invalidRequest = new WasteLogStartRequestDTO(null, "RT001", "W001"); 

        mockMvc.perform(post("/wastewise/admin/wastelogs/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("zoneId: Zone ID cannot be empty.")))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    void startCollection_InvalidInput_InvalidFormat() throws Exception {
        WasteLogStartRequestDTO invalidRequest = new WasteLogStartRequestDTO("Z01", "RT001", "W001");

        mockMvc.perform(post("/wastewise/admin/wastelogs/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed: zoneId: Invalid Zone ID format. Must be like Z001.")) 
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void startCollection_BusinessLogicError_ActiveLogExists() throws Exception {
        when(wasteLogService.startCollection(any(WasteLogStartRequestDTO.class)))
                .thenThrow(new InvalidInputException(String.format(WasteLogConstants.ACTIVE_LOG_EXISTS_MESSAGE, "W001", "Z001", "RT001")));

        mockMvc.perform(post("/wastewise/admin/wastelogs/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(startRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(String.format(WasteLogConstants.ACTIVE_LOG_EXISTS_MESSAGE, "W001", "Z001", "RT001")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // --- endCollection Tests ---

    @Test
    void endCollection_Success() throws Exception {
        when(wasteLogService.endCollection(any(WasteLogUpdateRequestDTO.class))).thenReturn(successEndResponseDTO);

        mockMvc.perform(put("/wastewise/admin/wastelogs/end")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(WasteLogConstants.WASTE_COLLECTION_LOG_COMPLETED_SUCCESSFULLY))
                .andExpect(jsonPath("$.data.logId").value(123L))
                .andExpect(jsonPath("$.data.collectionEndTime").exists())
                .andExpect(jsonPath("$.data.weightCollected").value(150.0));
    }

    @Test
    void endCollection_InvalidInput_NegativeWeight() throws Exception {
        WasteLogUpdateRequestDTO invalidRequest = new WasteLogUpdateRequestDTO("W001", -10.0); 

        mockMvc.perform(put("/wastewise/admin/wastelogs/end")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed: weightCollected: Weight Collected must be a positive value.")) 
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    void endCollection_InvalidInput_NullWorkerId() throws Exception { 
        WasteLogUpdateRequestDTO invalidRequest = new WasteLogUpdateRequestDTO(null, 100.0);

        mockMvc.perform(put("/wastewise/admin/wastelogs/end")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("workerId: Worker ID cannot be null.")))
                .andExpect(jsonPath("$.message").value(containsString("workerId: Worker ID cannot be empty.")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void endCollection_ResourceNotFound() throws Exception {
        when(wasteLogService.endCollection(any(WasteLogUpdateRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException(String.format(WasteLogConstants.WASTE_LOG_NOT_FOUND_MESSAGE, "W999"))); 

        WasteLogUpdateRequestDTO notFoundRequest = new WasteLogUpdateRequestDTO("W999", 100.0);

        mockMvc.perform(put("/wastewise/admin/wastelogs/end")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notFoundRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(String.format(WasteLogConstants.WASTE_LOG_NOT_FOUND_MESSAGE, "W999")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void endCollection_InvalidInput_EndTimeBeforeStartTime() throws Exception {
        when(wasteLogService.endCollection(any(WasteLogUpdateRequestDTO.class)))
                .thenThrow(new InvalidInputException(WasteLogConstants.COLLECTION_END_TIME_BEFORE_START_TIME));

        mockMvc.perform(put("/wastewise/admin/wastelogs/end")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(WasteLogConstants.COLLECTION_END_TIME_BEFORE_START_TIME))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // --- getZoneLogs Tests ---

    @Test
    void getZoneLogs_Success_WithData() throws Exception {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        ZoneReportDTO report1 = new ZoneReportDTO("Z001", LocalDate.of(2023, 1, 10), 2L, 200.0);
        ZoneReportDTO report2 = new ZoneReportDTO("Z001", LocalDate.of(2023, 1, 15), 1L, 150.0);
        List<ZoneReportDTO> reportsList = List.of(report1, report2);
        
        Pageable pageable = PageRequest.of(0, 1, org.springframework.data.domain.Sort.by("date").ascending());
        Page<ZoneReportDTO> reportsPage = new PageImpl<>(reportsList, pageable, reportsList.size());

        when(wasteLogService.getZoneLogs(eq("Z001"), eq(startDate), eq(endDate), any(Pageable.class)))
                .thenReturn(reportsPage);

        mockMvc.perform(get("/wastewise/admin/wastelogs/reports/zone") 
                .param("zoneId", "Z001") 
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .param("page", "0")
                .param("size", "1")
                .param("sort", "date,asc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Zone report generated successfully."))
                .andExpect(jsonPath("$.data.content[0].zoneId").value("Z001"))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    @Test
    void getZoneLogs_Success_NoData() throws Exception {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        Pageable pageable = PageRequest.of(0, 1, org.springframework.data.domain.Sort.by("date").ascending());
        Page<ZoneReportDTO> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(wasteLogService.getZoneLogs(eq("Z001"), eq(startDate), eq(endDate), any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/wastewise/admin/wastelogs/reports/zone")
                .param("zoneId", "Z001")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .param("page", "0")
                .param("size", "1")
                .param("sort", "date,asc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(String.format(WasteLogConstants.NO_COMPLETED_LOGS_FOUND_ZONE, "Z001", startDate.toString(), endDate.toString())))
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    void getZoneLogs_InvalidZoneIdFormat() throws Exception {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);

        mockMvc.perform(get("/wastewise/admin/wastelogs/reports/zone")
                .param("zoneId", "INVALID_ZONE") 
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .param("page", "0")
                .param("size", "1")
                .param("sort", "date,asc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed: getZoneLogs.zoneId: Invalid Zone ID format. Must be Z### (e.g., Z001).")) 
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getZoneLogs_InvalidDateRange() throws Exception {
        LocalDate startDate = LocalDate.of(2023, 1, 31);
        LocalDate endDate = LocalDate.of(2023, 1, 1);

        when(wasteLogService.getZoneLogs(eq("Z001"), eq(startDate), eq(endDate), any(Pageable.class)))
                .thenThrow(new InvalidInputException(WasteLogConstants.END_DATE_CANNOT_BE_BEFORE_START_DATE));

        mockMvc.perform(get("/wastewise/admin/wastelogs/reports/zone")
                .param("zoneId", "Z001")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .param("page", "0")
                .param("size", "1")
                .param("sort", "date,asc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(WasteLogConstants.END_DATE_CANNOT_BE_BEFORE_START_DATE))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getZoneLogs_InvalidDateFormat() throws Exception {
        mockMvc.perform(get("/wastewise/admin/wastelogs/reports/zone")
                .param("zoneId", "Z001")
                .param("startDate", "2023/01/01") 
                .param("endDate", "2023-01-31")
                .param("page", "0")
                .param("size", "1")
                .param("sort", "date,asc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                // Corrected expected message based on the actual error you provided
                .andExpect(jsonPath("$.message").value("Parameter 'startDate' has invalid value '2023/01/01'. Expected type is 'LocalDate'."))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // --- getVehicleLogs Tests ---

    @Test
    void getVehicleLogs_Success_WithData() throws Exception {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        VehicleReportDTO report1 = new VehicleReportDTO("RT001", "Z001", 100.0, LocalDate.of(2023, 1, 5));
        VehicleReportDTO report2 = new VehicleReportDTO("RT001", "Z002", 120.0, LocalDate.of(2023, 1, 12));
        List<VehicleReportDTO> reportsList = List.of(report1, report2);
        
        Pageable pageable = PageRequest.of(0, 1, org.springframework.data.domain.Sort.by("collectionDate").ascending());
        Page<VehicleReportDTO> reportsPage = new PageImpl<>(reportsList, pageable, reportsList.size());

        when(wasteLogService.getVehicleLogs(eq("RT001"), eq(startDate), eq(endDate), any(Pageable.class)))
                .thenReturn(reportsPage);

        mockMvc.perform(get("/wastewise/admin/wastelogs/reports/vehicle") 
                .param("vehicleId", "RT001") 
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .param("page", "0")
                .param("size", "1")
                .param("sort", "collectionDate,asc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(WasteLogConstants.VEHICLE_REPORT_GENERATED_SUCCESSFULLY))
                .andExpect(jsonPath("$.data.content[0].vehicleId").value("RT001"))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    @Test
    void getVehicleLogs_Success_NoData() throws Exception {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        Pageable pageable = PageRequest.of(0, 1, org.springframework.data.domain.Sort.by("collectionDate").ascending());
        Page<VehicleReportDTO> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(wasteLogService.getVehicleLogs(eq("RT001"), eq(startDate), eq(endDate), any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/wastewise/admin/wastelogs/reports/vehicle")
                .param("vehicleId", "RT001")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .param("page", "0")
                .param("size", "1")
                .param("sort", "collectionDate,asc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(String.format(WasteLogConstants.NO_COMPLETED_LOGS_FOUND_VEHICLE, "RT001", startDate.toString(), endDate.toString())))
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    void getVehicleLogs_InvalidVehicleIdFormat() throws Exception {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);

        mockMvc.perform(get("/wastewise/admin/wastelogs/reports/vehicle")
                .param("vehicleId", "INVALID_VEHICLE") 
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .param("page", "0")
                .param("size", "1")
                .param("sort", "collectionDate,asc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed: getVehicleLogs.vehicleId: Invalid Vehicle ID format. Must be RT### or PT### (e.g., RT001).")) 
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getVehicleLogs_InvalidDateRange() throws Exception {
        LocalDate startDate = LocalDate.of(2023, 1, 31);
        LocalDate endDate = LocalDate.of(2023, 1, 1);

        when(wasteLogService.getVehicleLogs(eq("RT001"), eq(startDate), eq(endDate), any(Pageable.class)))
                .thenThrow(new InvalidInputException(WasteLogConstants.END_DATE_CANNOT_BE_BEFORE_START_DATE));

        mockMvc.perform(get("/wastewise/admin/wastelogs/reports/vehicle")
                .param("vehicleId", "RT001")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .param("page", "0")
                .param("size", "1")
                .param("sort", "collectionDate,asc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(WasteLogConstants.END_DATE_CANNOT_BE_BEFORE_START_DATE))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getVehicleLogs_InvalidDateFormat() throws Exception {
        mockMvc.perform(get("/wastewise/admin/wastelogs/reports/vehicle")
                .param("vehicleId", "RT001")
                .param("startDate", "invalid-date") 
                .param("endDate", "2023-01-31")
                .param("page", "0")
                .param("size", "1")
                .param("sort", "collectionDate,asc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                // Corrected expected message based on the actual error you provided
                .andExpect(jsonPath("$.message").value("Parameter 'startDate' has invalid value 'invalid-date'. Expected type is 'LocalDate'."))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}