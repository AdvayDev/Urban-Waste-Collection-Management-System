package com.wastewise.worker.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wastewise.worker.management.dto.*;
import com.wastewise.worker.management.service.serviceimpl.WorkerAssignmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(WorkerAssignmentController.class)
class WorkerAssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkerAssignmentServiceImpl workerAssignmentServiceImpl;

    void testWorkerAssignments() throws Exception {
        WorkerAssignmentDTO dto = new WorkerAssignmentDTO("A001", "W001", "Z001", "Z001-R001", "DAY");
        when(workerAssignmentServiceImpl.findAllWorkerAssignments()).thenReturn(List.of(dto));

        mockMvc.perform(get("/wastewise/admin/worker-assignments"))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$[0].assignmentId").value("A001"));
    }

    @Test
    void testAssignWorkerToAssignment() throws Exception {
        WorkerAssignmentDTO dto = new WorkerAssignmentDTO("A001", "W001", "Z001", "Z001-R001", "DAY");
        when(workerAssignmentServiceImpl.assignWorkerToAssignment(any(), any(), any()))
                .thenReturn("Worker assigned successfully");

        mockMvc.perform(post("/wastewise/admin/worker-assignments/A001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Worker assigned successfully"));
    }


    @Test
    void testUpdateWorkerAssignment() throws Exception {
        UpdateWorkerAssignDTO dto = new UpdateWorkerAssignDTO("W001", "W002");
        when(workerAssignmentServiceImpl.updateSingleWorkerAssignment(any(), any(), any()))
                .thenReturn("Worker assignment updated successfully");

        mockMvc.perform(put("/wastewise/admin/worker-assignments/update/A001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Worker assignment updated successfully"));
    }

    @Test
    void testUpdateBothWorkerAssignments() throws Exception {
        WorkerReassignRequestDTO dto = new WorkerReassignRequestDTO("W001", "W002", "W003", "W004");
        when(workerAssignmentServiceImpl.updateBothWorkerAssignments(any(), any(), any(), any(), any()))
                .thenReturn("Both worker assignments updated successfully");

        mockMvc.perform(put("/wastewise/admin/worker-assignments/reassign/A001/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Both worker assignments updated successfully"));
    }

    @Test
    void testDeleteWorkerAssignment() throws Exception {
        when(workerAssignmentServiceImpl.deleteWorkerAssignment("A001"))
                .thenReturn("Deleted assignments and updated worker statuses");

        mockMvc.perform(delete("/wastewise/admin/worker-assignments/A001"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted assignments and updated worker statuses"));
    }

    @Test
    void testAssignWorkerToAssignment_validationFailure() throws Exception {
        // WorkerAssignmentDTO with missing required fields (e.g., workerId null)
        WorkerAssignmentDTO dto = new WorkerAssignmentDTO();
        dto.setAssignmentId("A001"); // valid
        dto.setWorkerId(null);       // invalid

        mockMvc.perform(post("/wastewise/admin/worker-assignments/A001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Invalid input provided")));
    }
}
