package com.wastewise.worker.management.service;

import com.wastewise.worker.management.dto.*;
import com.wastewise.worker.management.enums.WorkerStatus;
import com.wastewise.worker.management.exception.AuthenticationFailedException;
import com.wastewise.worker.management.exception.ContactInformationUsedException;
import com.wastewise.worker.management.exception.WorkerNotFoundException;
import com.wastewise.worker.management.mapper.WorkerMapper;
import com.wastewise.worker.management.model.Worker;
import com.wastewise.worker.management.repository.WorkerRepository;
import com.wastewise.worker.management.service.serviceimpl.WorkerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkerServiceImplTest {

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private WorkerMapper workerMapper;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WorkerServiceImpl workerService;

    private Worker worker;
    private WorkerDTO workerDTO;
    private WorkerCreateDTO createDTO;
    private WorkerUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        worker = new Worker();
        worker.setWorkerId("W001");
        worker.setWorkerStatus(WorkerStatus.AVAILABLE);

        workerDTO = new WorkerDTO();
        workerDTO.setWorkerId("W001");

        createDTO = new WorkerCreateDTO("Alice", "1234567890", "alice@example.com", "003", "AVAILABLE");
        updateDTO = new WorkerUpdateDTO("Alice", "1234567890", "alice@example.com", "003", "AVAILABLE");

        // Set private field authServiceUrl via reflection
        try {
            var field = WorkerServiceImpl.class.getDeclaredField("authServiceUrl");
            field.setAccessible(true);
            field.set(workerService, "http://auth-service");
        } catch (Exception e) {
            throw new RuntimeException("Failed to set authServiceUrl", e);
        }
    }

    @Test
    void testGenerateWorkerId() {
        when(workerRepository.countAll()).thenReturn(5L);
        String workerId = workerService.generateWorkerId();
        assertEquals("W006", workerId);
    }

    @Test
    void testCreateWorker_AuthServiceSuccess() {
        when(workerRepository.countAll()).thenReturn(0L);
        when(workerRepository.existsByContactNumber(createDTO.getContactNumber())).thenReturn(false);
        when(workerRepository.existsByContactEmail(createDTO.getContactEmail())).thenReturn(false);
        when(workerMapper.toEntity(createDTO)).thenReturn(worker);

        doAnswer(invocation -> {
            worker.setWorkerStatus(WorkerStatus.valueOf(createDTO.getWorkerStatus()));
            worker.setWorkerId("W001");
            worker.setCreatedDate(LocalDateTime.now());
            return null;
        }).when(workerRepository).save(any(Worker.class));

        when(restTemplate.postForEntity(anyString(), any(RegisterWorkerDTO.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("OK", HttpStatus.OK));

        String result = workerService.createWorker(createDTO);
        assertTrue(result.contains("Created worker with id W001"));
    }

    @Test
    void testCreateWorker_AuthServiceFails() {
        when(workerRepository.countAll()).thenReturn(0L);
        when(workerRepository.existsByContactNumber(createDTO.getContactNumber())).thenReturn(false);
        when(workerRepository.existsByContactEmail(createDTO.getContactEmail())).thenReturn(false);
        when(workerMapper.toEntity(createDTO)).thenReturn(worker);
        when(workerRepository.save(any(Worker.class))).thenReturn(worker);

        when(restTemplate.postForEntity(anyString(), any(RegisterWorkerDTO.class), eq(String.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        assertThrows(AuthenticationFailedException.class, () -> workerService.createWorker(createDTO));
        verify(workerRepository).save(worker); // ensure save was called
    }


    @Test
    void testCreateWorker_ThrowsContactNumberUsedException() {
        when(workerRepository.existsByContactNumber(createDTO.getContactNumber())).thenReturn(true);
        assertThrows(ContactInformationUsedException.class, () -> workerService.createWorker(createDTO));
    }

    @Test
    void testCreateWorker_ThrowsContactEmailUsedException() {
        when(workerRepository.existsByContactNumber(createDTO.getContactNumber())).thenReturn(false);
        when(workerRepository.existsByContactEmail(createDTO.getContactEmail())).thenReturn(true);
        assertThrows(ContactInformationUsedException.class, () -> workerService.createWorker(createDTO));
    }

    @Test
    void testGetAllWorkers() {
        when(workerRepository.findAll()).thenReturn(List.of(worker));
        when(workerMapper.toDTO(worker)).thenReturn(workerDTO);
        List<WorkerDTO> result = workerService.getAllWorkers();
        assertEquals(1, result.size());
    }

    @Test
    void testGetWorker_Success() {
        when(workerRepository.findById("W001")).thenReturn(Optional.of(worker));
        when(workerMapper.toDTO(worker)).thenReturn(workerDTO);
        WorkerDTO result = workerService.getWorker("W001");
        assertEquals("W001", result.getWorkerId());
    }

    @Test
    void testGetWorker_NotFound() {
        when(workerRepository.findById("W999")).thenReturn(Optional.empty());
        assertThrows(WorkerNotFoundException.class, () -> workerService.getWorker("W999"));
    }

    @Test
    void testGetWorkerIds() {
        when(workerRepository.findAllWorkerId()).thenReturn(List.of("W001", "W002"));
        List<String> ids = workerService.getWorkerIds();
        assertEquals(2, ids.size());
    }

    @Test
    void testGetAllAvailableWorkerIds() {
        WorkerInfoDTO infoDTO = new WorkerInfoDTO("W001", "Alice");
        when(workerRepository.findAvailableWorkersByRole()).thenReturn(List.of(infoDTO));
        List<WorkerInfoDTO> result = workerService.getAllAvailableWorkerIds();
        assertEquals(1, result.size());
        assertEquals("W001", result.get(0).getWorkerId());
    }

    @Test
    void testUpdateWorker_Success() {
        when(workerRepository.findById("W001")).thenReturn(Optional.of(worker));
        when(workerRepository.existsByContactNumberAndWorkerIdNot(updateDTO.getContactNumber(), "W001")).thenReturn(false);
        when(workerRepository.existsByContactEmailAndWorkerIdNot(updateDTO.getContactEmail(), "W001")).thenReturn(false);
        String result = workerService.updateWorker("W001", updateDTO);
        assertTrue(result.contains("Updated worker with id W001"));
    }

    @Test
    void testChangeWorkerStatus_Success() {
        when(workerRepository.findById("W001")).thenReturn(Optional.of(worker));
        String result = workerService.changeWorkerStatus("W001", WorkerStatus.OCCUPIED);
        assertTrue(result.contains("Status of worker with id W001 changed successfully"));
    }

    @Test
    void testUpdateWorker_ThrowsWorkerNotFoundException() {
        when(workerRepository.findById("W999")).thenReturn(Optional.empty());
        assertThrows(WorkerNotFoundException.class, () -> workerService.updateWorker("W999", updateDTO));
    }

    @Test
    void testUpdateWorker_ThrowsContactNumberUsedException() {
        when(workerRepository.findById("W001")).thenReturn(Optional.of(worker));
        when(workerRepository.existsByContactNumberAndWorkerIdNot(updateDTO.getContactNumber(), "W001")).thenReturn(true);
        assertThrows(ContactInformationUsedException.class, () -> workerService.updateWorker("W001", updateDTO));
    }

    @Test
    void testUpdateWorker_ThrowsContactEmailUsedException() {
        when(workerRepository.findById("W001")).thenReturn(Optional.of(worker));
        when(workerRepository.existsByContactNumberAndWorkerIdNot(updateDTO.getContactNumber(), "W001")).thenReturn(false);
        when(workerRepository.existsByContactEmailAndWorkerIdNot(updateDTO.getContactEmail(), "W001")).thenReturn(true);
        assertThrows(ContactInformationUsedException.class, () -> workerService.updateWorker("W001", updateDTO));
    }

    @Test
    void testChangeWorkerStatus_ThrowsWorkerNotFoundException() {
        when(workerRepository.findById("W999")).thenReturn(Optional.empty());
        assertThrows(WorkerNotFoundException.class, () -> workerService.changeWorkerStatus("W999", WorkerStatus.OCCUPIED));
    }
}