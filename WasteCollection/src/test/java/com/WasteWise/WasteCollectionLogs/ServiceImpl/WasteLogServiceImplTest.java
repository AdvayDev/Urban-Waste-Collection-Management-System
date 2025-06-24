package com.WasteWise.WasteCollectionLogs.ServiceImpl;

import com.WasteWise.WasteCollectionLogs.Constants.WasteLogConstants;
import com.WasteWise.WasteCollectionLogs.Dto.WasteLogResponseDTO;
import com.WasteWise.WasteCollectionLogs.Dto.WasteLogStartRequestDTO;
import com.WasteWise.WasteCollectionLogs.Dto.WasteLogUpdateRequestDTO;
import com.WasteWise.WasteCollectionLogs.Dto.ZoneReportDTO;
import com.WasteWise.WasteCollectionLogs.Dto.VehicleReportDTO;
import com.WasteWise.WasteCollectionLogs.Handler.InvalidInputException;
import com.WasteWise.WasteCollectionLogs.Handler.ResourceNotFoundException;
import com.WasteWise.WasteCollectionLogs.Model.WasteLog;
import com.WasteWise.WasteCollectionLogs.Repository.WasteLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WasteLogServiceImpl Unit Tests")
class WasteLogServiceImplTest {

    @Mock
    private WasteLogRepository wasteLogRepository;

    @InjectMocks
    private WasteLogServiceImpl wasteLogService;

    @BeforeEach
    void setUp() {
        // No specific setup needed for each test method if mocks are clean.
        // MockitoExtension handles mock initialization and reset.
    }

    // --- startCollection Tests ---

    @Test
    @DisplayName("startCollection: Should successfully start a new collection log")
    void startCollection_Success() {
        // Arrange
        WasteLogStartRequestDTO request = new WasteLogStartRequestDTO("Z001", "RT001", "W001");
        WasteLog newLog = new WasteLog();
        newLog.setLogId(1L);
        newLog.setZoneId("Z001");
        newLog.setVehicleId("RT001");
        newLog.setWorkerId("W001");
        newLog.setCollectionStartTime(LocalDateTime.now()); 
        newLog.setCreatedDate(LocalDateTime.now()); 

        when(wasteLogRepository.save(any(WasteLog.class))).thenReturn(newLog);

        WasteLogResponseDTO response = wasteLogService.startCollection(request);
        
        assertNotNull(response);
        assertEquals(1L, response.getLogId());
        assertEquals(WasteLogConstants.WASTE_COLLECTION_LOG_RECORDED_SUCCESSFULLY, response.getMessage());
        verify(wasteLogRepository, times(1)).save(any(WasteLog.class));
    }

    // --- endCollection Tests ---

    @Test
    @DisplayName("endCollection: Should successfully end an active collection log")
    void endCollection_Success() {
      
        WasteLogUpdateRequestDTO request = new WasteLogUpdateRequestDTO("W001", 150.0);
        WasteLog existingLog = new WasteLog(); // Using no-args constructor
        existingLog.setLogId(1L);
        existingLog.setWorkerId("W001");
        existingLog.setZoneId("Z001");
        existingLog.setVehicleId("RT001"); 
        existingLog.setCollectionStartTime(LocalDateTime.now().minusHours(1)); 
        existingLog.setCollectionEndTime(null);
        existingLog.setWeightCollected(0.0); 
        existingLog.setCreatedDate(LocalDateTime.now().minusHours(2)); 


        when(wasteLogRepository.findByWorkerIdAndCollectionEndTimeIsNull(request.getWorkerId()))
                .thenReturn(Optional.of(existingLog));
        when(wasteLogRepository.save(any(WasteLog.class))).thenReturn(existingLog); // Mock save after update

        // Act
        WasteLogResponseDTO response = wasteLogService.endCollection(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getLogId());
        assertEquals(WasteLogConstants.WASTE_COLLECTION_LOG_COMPLETED_SUCCESSFULLY, response.getMessage());
        assertNotNull(existingLog.getCollectionEndTime());
        assertEquals(150.0, existingLog.getWeightCollected());
        assertNotNull(existingLog.getUpdatedDate()); 
        verify(wasteLogRepository, times(1)).findByWorkerIdAndCollectionEndTimeIsNull(request.getWorkerId());
        verify(wasteLogRepository, times(1)).save(existingLog);
    }

    @Test
    @DisplayName("endCollection: Should throw ResourceNotFoundException if no active log found for worker")
    void endCollection_NoActiveLog_ThrowsResourceNotFoundException() {
        // Arrange
        WasteLogUpdateRequestDTO request = new WasteLogUpdateRequestDTO("W001", 150.0);

        when(wasteLogRepository.findByWorkerIdAndCollectionEndTimeIsNull(request.getWorkerId()))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> wasteLogService.endCollection(request));

        assertEquals(String.format(WasteLogConstants.WASTE_LOG_NOT_FOUND_MESSAGE, request.getWorkerId()), exception.getMessage());
        verify(wasteLogRepository, times(1)).findByWorkerIdAndCollectionEndTimeIsNull(request.getWorkerId());
        verify(wasteLogRepository, never()).save(any(WasteLog.class)); // Ensure save is not called
    }

    @Test
    @DisplayName("endCollection: Should throw InvalidInputException if end time is before start time")
    void endCollection_EndTimeBeforeStartTime_ThrowsInvalidInputException() {
        // Arrange
        WasteLogUpdateRequestDTO request = new WasteLogUpdateRequestDTO("W001", 150.0);
        WasteLog existingLog = new WasteLog(); // Using no-args constructor
        existingLog.setLogId(1L);
        existingLog.setWorkerId("W001");
        existingLog.setZoneId("Z001");
        existingLog.setVehicleId("RT001");
        // Set collectionStartTime to a future time to force the error
        existingLog.setCollectionStartTime(LocalDateTime.now().plusHours(1));
        existingLog.setCollectionEndTime(null);
        existingLog.setWeightCollected(0.0);
        existingLog.setCreatedDate(LocalDateTime.now().minusHours(2));


        when(wasteLogRepository.findByWorkerIdAndCollectionEndTimeIsNull(request.getWorkerId()))
                .thenReturn(Optional.of(existingLog));

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> wasteLogService.endCollection(request));

        assertEquals(WasteLogConstants.COLLECTION_END_TIME_BEFORE_START_TIME, exception.getMessage());
        verify(wasteLogRepository, times(1)).findByWorkerIdAndCollectionEndTimeIsNull(request.getWorkerId());
        verify(wasteLogRepository, never()).save(any(WasteLog.class)); // Ensure save is not called
    }

    // --- getZoneLogs Tests ---

    @Test
    @DisplayName("getZoneLogs: Should return a paginated list of zone reports for valid date range")
    void getZoneLogs_ValidDateRange_ReturnsPaginatedReports() {
        // Arrange
        String zoneId = "Z001";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 3);
        Pageable pageable = PageRequest.of(0, 5);

        // Using no-args constructor and setting only relevant fields
        WasteLog log1 = new WasteLog();
        log1.setLogId(1L); log1.setZoneId("Z001"); log1.setVehicleId("RT001"); log1.setWorkerId("W001");
        log1.setCollectionStartTime(LocalDateTime.of(2024, 1, 1, 9, 0));
        log1.setCollectionEndTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        log1.setWeightCollected(100.0);
        log1.setCreatedDate(LocalDateTime.of(2024, 1, 1, 8, 0)); // Set for completeness, not for explicit testing of audit logic here

        WasteLog log2 = new WasteLog();
        log2.setLogId(2L); log2.setZoneId("Z001"); log2.setVehicleId("PT001"); log2.setWorkerId("W002");
        log2.setCollectionStartTime(LocalDateTime.of(2024, 1, 1, 11, 0));
        log2.setCollectionEndTime(LocalDateTime.of(2024, 1, 1, 12, 0));
        log2.setWeightCollected(150.0);
        log2.setCreatedDate(LocalDateTime.of(2024, 1, 1, 10, 0));

        WasteLog log3 = new WasteLog();
        log3.setLogId(3L); log3.setZoneId("Z001"); log3.setVehicleId("RT002"); log3.setWorkerId("W003");
        log3.setCollectionStartTime(LocalDateTime.of(2024, 1, 2, 9, 0));
        log3.setCollectionEndTime(LocalDateTime.of(2024, 1, 2, 10, 0));
        log3.setWeightCollected(200.0);
        log3.setCreatedDate(LocalDateTime.of(2024, 1, 2, 8, 0));

        WasteLog log4 = new WasteLog();
        log4.setLogId(4L); log4.setZoneId("Z001"); log4.setVehicleId("RT001"); log4.setWorkerId("W001");
        log4.setCollectionStartTime(LocalDateTime.of(2024, 1, 3, 9, 0));
        log4.setCollectionEndTime(LocalDateTime.of(2024, 1, 3, 10, 0));
        log4.setWeightCollected(50.0);
        log4.setCreatedDate(LocalDateTime.of(2024, 1, 3, 8, 0));

        // Incomplete log - collectionEndTime is null
        WasteLog log5Incomplete = new WasteLog();
        log5Incomplete.setLogId(5L); log5Incomplete.setZoneId("Z001"); log5Incomplete.setVehicleId("RT003"); log5Incomplete.setWorkerId("W004");
        log5Incomplete.setCollectionStartTime(LocalDateTime.of(2024, 1, 3, 11, 0));
        log5Incomplete.setCollectionEndTime(null);
        log5Incomplete.setWeightCollected(0.0); // Or null, depending on your default
        log5Incomplete.setCreatedDate(LocalDateTime.of(2024, 1, 3, 10, 0));


        List<WasteLog> allLogs = Arrays.asList(log1, log2, log3, log4, log5Incomplete);

        when(wasteLogRepository.findByZoneIdAndCollectionStartTimeBetween(
                eq(zoneId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(allLogs);

        // Act
        Page<ZoneReportDTO> resultPage = wasteLogService.getZoneLogs(zoneId, startDate, endDate, pageable);

        // Assert
        assertNotNull(resultPage);
        assertEquals(3, resultPage.getTotalElements()); // 3 unique dates with completed logs
        assertEquals(1, resultPage.getTotalPages());
        assertEquals(3, resultPage.getContent().size());

        ZoneReportDTO report1 = resultPage.getContent().get(0);
        assertEquals(LocalDate.of(2024, 1, 1), report1.getDate());
        assertEquals("Z001", report1.getZoneId());
        assertEquals(2L, report1.getTotalNumberOfCollections()); // RT001, PT001
        assertEquals(250.0, report1.getTotalWeightCollectedKg());

        ZoneReportDTO report2 = resultPage.getContent().get(1);
        assertEquals(LocalDate.of(2024, 1, 2), report2.getDate());
        assertEquals("Z001", report2.getZoneId());
        assertEquals(1L, report2.getTotalNumberOfCollections()); // RT002
        assertEquals(200.0, report2.getTotalWeightCollectedKg());

        ZoneReportDTO report3 = resultPage.getContent().get(2);
        assertEquals(LocalDate.of(2024, 1, 3), report3.getDate());
        assertEquals("Z001", report3.getZoneId());
        assertEquals(1L, report3.getTotalNumberOfCollections()); // RT001 (incomplete log not counted)
        assertEquals(50.0, report3.getTotalWeightCollectedKg());

        verify(wasteLogRepository, times(1)).findByZoneIdAndCollectionStartTimeBetween(
                eq(zoneId), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("getZoneLogs: Should return empty page if no logs found")
    void getZoneLogs_NoLogsFound_ReturnsEmptyPage() {
        // Arrange
        String zoneId = "Z005";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 3);
        Pageable pageable = PageRequest.of(0, 5);

        when(wasteLogRepository.findByZoneIdAndCollectionStartTimeBetween(
                eq(zoneId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        Page<ZoneReportDTO> resultPage = wasteLogService.getZoneLogs(zoneId, startDate, endDate, pageable);

        // Assert
        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty());
        assertEquals(0, resultPage.getTotalElements());
        assertEquals(0, resultPage.getTotalPages());
        verify(wasteLogRepository, times(1)).findByZoneIdAndCollectionStartTimeBetween(
                eq(zoneId), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("getZoneLogs: Should throw InvalidInputException if start date is after end date")
    void getZoneLogs_InvalidDateRange_ThrowsInvalidInputException() {
        // Arrange
        String zoneId = "Z001";
        LocalDate startDate = LocalDate.of(2024, 1, 5);
        LocalDate endDate = LocalDate.of(2024, 1, 1);
        Pageable pageable = PageRequest.of(0, 5);

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> wasteLogService.getZoneLogs(zoneId, startDate, endDate, pageable));

        assertEquals(WasteLogConstants.END_DATE_CANNOT_BE_BEFORE_START_DATE, exception.getMessage());
        verify(wasteLogRepository, never()).findByZoneIdAndCollectionStartTimeBetween(
                anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("getZoneLogs: Should handle pagination correctly for ZoneReportDTO")
    void getZoneLogs_PaginationHandledCorrectly() {
        // Arrange
        String zoneId = "Z001";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 5);
        Pageable pageable = PageRequest.of(0, 1); // Requesting 1 item per page

        WasteLog log1 = new WasteLog();
        log1.setLogId(1L); log1.setZoneId("Z001"); log1.setVehicleId("RT001"); log1.setWorkerId("W001");
        log1.setCollectionStartTime(LocalDateTime.of(2024, 1, 1, 9, 0));
        log1.setCollectionEndTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        log1.setWeightCollected(100.0);
        log1.setCreatedDate(LocalDateTime.of(2024, 1, 1, 8, 0));

        WasteLog log2 = new WasteLog();
        log2.setLogId(2L); log2.setZoneId("Z001"); log2.setVehicleId("PT001"); log2.setWorkerId("W002");
        log2.setCollectionStartTime(LocalDateTime.of(2024, 1, 2, 11, 0));
        log2.setCollectionEndTime(LocalDateTime.of(2024, 1, 2, 12, 0));
        log2.setWeightCollected(150.0);
        log2.setCreatedDate(LocalDateTime.of(2024, 1, 2, 10, 0));

        WasteLog log3 = new WasteLog();
        log3.setLogId(3L); log3.setZoneId("Z001"); log3.setVehicleId("RT002"); log3.setWorkerId("W003");
        log3.setCollectionStartTime(LocalDateTime.of(2024, 1, 3, 9, 0));
        log3.setCollectionEndTime(LocalDateTime.of(2024, 1, 3, 10, 0));
        log3.setWeightCollected(200.0);
        log3.setCreatedDate(LocalDateTime.of(2024, 1, 3, 8, 0));

        List<WasteLog> allLogs = Arrays.asList(log1, log2, log3);

        when(wasteLogRepository.findByZoneIdAndCollectionStartTimeBetween(
                eq(zoneId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(allLogs);

        // Act - Page 0
        Page<ZoneReportDTO> resultPage0 = wasteLogService.getZoneLogs(zoneId, startDate, endDate, pageable);

        // Assert - Page 0
        assertNotNull(resultPage0);
        assertEquals(3, resultPage0.getTotalElements());
        assertEquals(3, resultPage0.getTotalPages());
        assertEquals(1, resultPage0.getContent().size());
        assertEquals(LocalDate.of(2024, 1, 1), resultPage0.getContent().get(0).getDate());

        // Act - Page 1
        pageable = PageRequest.of(1, 1);
        Page<ZoneReportDTO> resultPage1 = wasteLogService.getZoneLogs(zoneId, startDate, endDate, pageable);

        // Assert - Page 1
        assertNotNull(resultPage1);
        assertEquals(3, resultPage1.getTotalElements());
        assertEquals(3, resultPage1.getTotalPages());
        assertEquals(1, resultPage1.getContent().size());
        assertEquals(LocalDate.of(2024, 1, 2), resultPage1.getContent().get(0).getDate());

        // Act - Page 2
        pageable = PageRequest.of(2, 1);
        Page<ZoneReportDTO> resultPage2 = wasteLogService.getZoneLogs(zoneId, startDate, endDate, pageable);

        // Assert - Page 2
        assertNotNull(resultPage2);
        assertEquals(3, resultPage2.getTotalElements());
        assertEquals(3, resultPage2.getTotalPages());
        assertEquals(1, resultPage2.getContent().size());
        assertEquals(LocalDate.of(2024, 1, 3), resultPage2.getContent().get(0).getDate());

        // Act - Page out of bounds
        pageable = PageRequest.of(3, 1);
        Page<ZoneReportDTO> resultPageOutOfBounds = wasteLogService.getZoneLogs(zoneId, startDate, endDate, pageable);

        // Assert - Page out of bounds
        assertNotNull(resultPageOutOfBounds);
        assertTrue(resultPageOutOfBounds.isEmpty());
        assertEquals(3, resultPageOutOfBounds.getTotalElements()); // Total elements should still be correct
        assertEquals(3, resultPageOutOfBounds.getTotalPages());
    }


    // --- getVehicleLogs Tests ---

    @Test
    @DisplayName("getVehicleLogs: Should return a paginated list of vehicle reports for valid date range")
    void getVehicleLogs_ValidDateRange_ReturnsPaginatedReports() {
        // Arrange
        String vehicleId = "RT001";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 3);
        Pageable pageable = PageRequest.of(0, 5);

        WasteLog log1 = new WasteLog();
        log1.setLogId(1L); log1.setZoneId("Z001"); log1.setVehicleId("RT001"); log1.setWorkerId("W001");
        log1.setCollectionStartTime(LocalDateTime.of(2024, 1, 1, 9, 0));
        log1.setCollectionEndTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        log1.setWeightCollected(100.0);
        log1.setCreatedDate(LocalDateTime.of(2024, 1, 1, 8, 0));

        WasteLog log2 = new WasteLog();
        log2.setLogId(2L); log2.setZoneId("Z002"); log2.setVehicleId("RT001"); log2.setWorkerId("W002");
        log2.setCollectionStartTime(LocalDateTime.of(2024, 1, 2, 11, 0));
        log2.setCollectionEndTime(LocalDateTime.of(2024, 1, 2, 12, 0));
        log2.setWeightCollected(150.0);
        log2.setCreatedDate(LocalDateTime.of(2024, 1, 2, 10, 0));

        WasteLog log3 = new WasteLog();
        log3.setLogId(3L); log3.setZoneId("Z001"); log3.setVehicleId("RT001"); log3.setWorkerId("W003");
        log3.setCollectionStartTime(LocalDateTime.of(2024, 1, 3, 9, 0));
        log3.setCollectionEndTime(LocalDateTime.of(2024, 1, 3, 10, 0));
        log3.setWeightCollected(50.0);
        log3.setCreatedDate(LocalDateTime.of(2024, 1, 3, 8, 0));

        // Incomplete log
        WasteLog log4Incomplete = new WasteLog();
        log4Incomplete.setLogId(4L); log4Incomplete.setZoneId("Z003"); log4Incomplete.setVehicleId("RT001"); log4Incomplete.setWorkerId("W004");
        log4Incomplete.setCollectionStartTime(LocalDateTime.of(2024, 1, 3, 11, 0));
        log4Incomplete.setCollectionEndTime(null);
        log4Incomplete.setWeightCollected(0.0);
        log4Incomplete.setCreatedDate(LocalDateTime.of(2024, 1, 3, 10, 0));

        List<WasteLog> allLogs = Arrays.asList(log1, log2, log3, log4Incomplete);

        when(wasteLogRepository.findByVehicleIdAndCollectionStartTimeBetween(
                eq(vehicleId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(allLogs);

        // Act
        Page<VehicleReportDTO> resultPage = wasteLogService.getVehicleLogs(vehicleId, startDate, endDate, pageable);

        // Assert
        assertNotNull(resultPage);
        assertEquals(3, resultPage.getTotalElements()); // Only completed logs
        assertEquals(1, resultPage.getTotalPages());
        assertEquals(3, resultPage.getContent().size());

        VehicleReportDTO report1 = resultPage.getContent().get(0);
        assertEquals("RT001", report1.getVehicleId());
        assertEquals("Z001", report1.getZoneId());
        assertEquals(100.0, report1.getWeightCollected());
        assertEquals(LocalDate.of(2024, 1, 1), report1.getCollectionDate());

        VehicleReportDTO report2 = resultPage.getContent().get(1);
        assertEquals("RT001", report2.getVehicleId());
        assertEquals("Z002", report2.getZoneId());
        assertEquals(150.0, report2.getWeightCollected());
        assertEquals(LocalDate.of(2024, 1, 2), report2.getCollectionDate());

        VehicleReportDTO report3 = resultPage.getContent().get(2);
        assertEquals("RT001", report3.getVehicleId());
        assertEquals("Z001", report3.getZoneId());
        assertEquals(50.0, report3.getWeightCollected());
        assertEquals(LocalDate.of(2024, 1, 3), report3.getCollectionDate());

        verify(wasteLogRepository, times(1)).findByVehicleIdAndCollectionStartTimeBetween(
                eq(vehicleId), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("getVehicleLogs: Should return empty page if no logs found")
    void getVehicleLogs_NoLogsFound_ReturnsEmptyPage() {
        // Arrange
        String vehicleId = "PT005";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 3);
        Pageable pageable = PageRequest.of(0, 5);

        when(wasteLogRepository.findByVehicleIdAndCollectionStartTimeBetween(
                eq(vehicleId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        Page<VehicleReportDTO> resultPage = wasteLogService.getVehicleLogs(vehicleId, startDate, endDate, pageable);

        // Assert
        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty());
        assertEquals(0, resultPage.getTotalElements());
        assertEquals(0, resultPage.getTotalPages());
        verify(wasteLogRepository, times(1)).findByVehicleIdAndCollectionStartTimeBetween(
                eq(vehicleId), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("getVehicleLogs: Should throw InvalidInputException if start date is after end date")
    void getVehicleLogs_InvalidDateRange_ThrowsInvalidInputException() {
        // Arrange
        String vehicleId = "RT001";
        LocalDate startDate = LocalDate.of(2024, 1, 5);
        LocalDate endDate = LocalDate.of(2024, 1, 1);
        Pageable pageable = PageRequest.of(0, 5);

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> wasteLogService.getVehicleLogs(vehicleId, startDate, endDate, pageable));

        assertEquals(WasteLogConstants.END_DATE_CANNOT_BE_BEFORE_START_DATE, exception.getMessage());
        verify(wasteLogRepository, never()).findByVehicleIdAndCollectionStartTimeBetween(
                anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("getVehicleLogs: Should handle pagination correctly for VehicleReportDTO")
    void getVehicleLogs_PaginationHandledCorrectly() {
        // Arrange
        String vehicleId = "RT001";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 5);
        Pageable pageable = PageRequest.of(0, 1); // Requesting 1 item per page

        WasteLog log1 = new WasteLog();
        log1.setLogId(1L); log1.setZoneId("Z001"); log1.setVehicleId("RT001"); log1.setWorkerId("W001");
        log1.setCollectionStartTime(LocalDateTime.of(2024, 1, 1, 9, 0));
        log1.setCollectionEndTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        log1.setWeightCollected(100.0);
        log1.setCreatedDate(LocalDateTime.of(2024, 1, 1, 8, 0));

        WasteLog log2 = new WasteLog();
        log2.setLogId(2L); log2.setZoneId("Z002"); log2.setVehicleId("RT001"); log2.setWorkerId("W002");
        log2.setCollectionStartTime(LocalDateTime.of(2024, 1, 2, 11, 0));
        log2.setCollectionEndTime(LocalDateTime.of(2024, 1, 2, 12, 0));
        log2.setWeightCollected(150.0);
        log2.setCreatedDate(LocalDateTime.of(2024, 1, 2, 10, 0));

        WasteLog log3 = new WasteLog();
        log3.setLogId(3L); log3.setZoneId("Z001"); log3.setVehicleId("RT001"); log3.setWorkerId("W003");
        log3.setCollectionStartTime(LocalDateTime.of(2024, 1, 3, 9, 0));
        log3.setCollectionEndTime(LocalDateTime.of(2024, 1, 3, 10, 0));
        log3.setWeightCollected(50.0);
        log3.setCreatedDate(LocalDateTime.of(2024, 1, 3, 8, 0));

        List<WasteLog> allLogs = Arrays.asList(log1, log2, log3);

        when(wasteLogRepository.findByVehicleIdAndCollectionStartTimeBetween(
                eq(vehicleId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(allLogs);

        // Act - Page 0
        Page<VehicleReportDTO> resultPage0 = wasteLogService.getVehicleLogs(vehicleId, startDate, endDate, pageable);

        // Assert - Page 0
        assertNotNull(resultPage0);
        assertEquals(3, resultPage0.getTotalElements());
        assertEquals(3, resultPage0.getTotalPages());
        assertEquals(1, resultPage0.getContent().size());
        assertEquals(LocalDate.of(2024, 1, 1), resultPage0.getContent().get(0).getCollectionDate());

        // Act - Page 1
        pageable = PageRequest.of(1, 1);
        Page<VehicleReportDTO> resultPage1 = wasteLogService.getVehicleLogs(vehicleId, startDate, endDate, pageable);

        // Assert - Page 1
        assertNotNull(resultPage1);
        assertEquals(3, resultPage1.getTotalElements());
        assertEquals(3, resultPage1.getTotalPages());
        assertEquals(1, resultPage1.getContent().size());
        assertEquals(LocalDate.of(2024, 1, 2), resultPage1.getContent().get(0).getCollectionDate());

        // Act - Page 2
        pageable = PageRequest.of(2, 1);
        Page<VehicleReportDTO> resultPage2 = wasteLogService.getVehicleLogs(vehicleId, startDate, endDate, pageable);

        // Assert - Page 2
        assertNotNull(resultPage2);
        assertEquals(3, resultPage2.getTotalElements());
        assertEquals(3, resultPage2.getTotalPages());
        assertEquals(1, resultPage2.getContent().size());
        assertEquals(LocalDate.of(2024, 1, 3), resultPage2.getContent().get(0).getCollectionDate());

        // Act - Page out of bounds
        pageable = PageRequest.of(3, 1);
        Page<VehicleReportDTO> resultPageOutOfBounds = wasteLogService.getVehicleLogs(vehicleId, startDate, endDate, pageable);

        // Assert - Page out of bounds
        assertNotNull(resultPageOutOfBounds);
        assertTrue(resultPageOutOfBounds.isEmpty());
        assertEquals(3, resultPageOutOfBounds.getTotalElements()); // Total elements should still be correct
        assertEquals(3, resultPageOutOfBounds.getTotalPages());
    }
}