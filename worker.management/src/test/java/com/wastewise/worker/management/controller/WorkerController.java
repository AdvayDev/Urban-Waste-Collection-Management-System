package com.wastewise.worker.management.controller;

import com.wastewise.worker.management.dto.*;
import com.wastewise.worker.management.enums.WorkerStatus;
import com.wastewise.worker.management.service.serviceimpl.WorkerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(WorkerController.class)
class WorkerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkerServiceImpl workerServiceImpl;

    @Test
    void testCreateWorker() throws Exception {
        WorkerCreateDTO dto = new WorkerCreateDTO("Alice", "1234567890", "alice@example.com", "003", "AVAILABLE");
        when(workerServiceImpl.createWorker(any())).thenReturn("Created worker with id W001");

        mockMvc.perform(post("/wastewise/admin/workers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Created worker with id W001"));
    }

    @Test
    void testFindAllWorkers() throws Exception {
        WorkerDTO dto = new WorkerDTO("W001", "Alice", "1234567890", "alice@example.com", WorkerStatus.AVAILABLE);
        when(workerServiceImpl.getAllWorkers()).thenReturn(List.of(dto));

        mockMvc.perform(get("/wastewise/admin/workers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].workerId").value("W001"));
    }

    @Test
    void testGetWorkerById() throws Exception {
        WorkerDTO dto = new WorkerDTO("W001", "Alice", "1234567890", "alice@example.com", WorkerStatus.AVAILABLE);
        when(workerServiceImpl.getWorker("W001")).thenReturn(dto);

        mockMvc.perform(get("/wastewise/admin/workers/W001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workerId").value("W001"));
    }

    @Test
    void testGetAllWorkerIds() throws Exception {
        when(workerServiceImpl.getWorkerIds()).thenReturn(List.of("W001", "W002"));

        mockMvc.perform(get("/wastewise/admin/workers/ids"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("W001"))
                .andExpect(jsonPath("$[1]").value("W002"));
    }

    @Test
    void testGetAvailableWorkerIds() throws Exception {
        WorkerInfoDTO dto = new WorkerInfoDTO("W001", "Alice");
        when(workerServiceImpl.getAllAvailableWorkerIds()).thenReturn(List.of(dto));

        mockMvc.perform(get("/wastewise/admin/workers/ids/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].workerId").value("W001"))
                .andExpect(jsonPath("$[0].name").value("Alice"));

    }

    @Test
    void testUpdateWorker() throws Exception {
        WorkerUpdateDTO dto = new WorkerUpdateDTO("Alice", "1234567890", "alice@example.com", "003", "AVAILABLE");
        when(workerServiceImpl.updateWorker("W001", dto)).thenReturn("Updated worker with id W001");

        mockMvc.perform(put("/wastewise/admin/workers/W001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated worker with id W001"));
    }

    @Test
    void testUpdateWorkerStatus() throws Exception {
        when(workerServiceImpl.changeWorkerStatus("W001", WorkerStatus.OCCUPIED))
                .thenReturn("Status of worker with id W001 changed successfully");

        mockMvc.perform(patch("/wastewise/admin/workers/status/W001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(WorkerStatus.OCCUPIED)))
                .andExpect(status().isOk())
                .andExpect(content().string("Status of worker with id W001 changed successfully"));
    }
}
