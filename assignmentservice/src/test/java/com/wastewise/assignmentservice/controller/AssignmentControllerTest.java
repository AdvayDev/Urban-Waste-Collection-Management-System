
package com.wastewise.assignmentservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wastewise.assignmentservice.config.TestSecurityConfig;
import com.wastewise.assignmentservice.dto.AssignmentDTO;
import com.wastewise.assignmentservice.service.AssignmentService;

@WebMvcTest(AssignmentController.class)
@Import(TestSecurityConfig.class)
class AssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssignmentService assignmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAssignment() throws Exception {
        AssignmentDTO dto = new AssignmentDTO("A001", "V001", "R001", LocalDate.now());

        Mockito.when(assignmentService.createAssignment(any())).thenReturn(dto);

        mockMvc.perform(post("/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.assignmentId").value("A001"));
    }

    @Test
    void testGetAllAssignments() throws Exception {
        AssignmentDTO dto = new AssignmentDTO("A001", "V001", "R001", LocalDate.now());
        Mockito.when(assignmentService.getAllAssignments()).thenReturn(List.of(dto));

        mockMvc.perform(get("/assignments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assignmentId").value("A001"));
    }

    @Test
    void testGetAssignmentById() throws Exception {
        AssignmentDTO dto = new AssignmentDTO("A001", "V001", "R001", LocalDate.now());
        Mockito.when(assignmentService.getAssignmentById("A001")).thenReturn(dto);

        mockMvc.perform(get("/assignments/A001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignmentId").value("A001"));
    }

    @Test
    void testUpdateAssignment() throws Exception {
        AssignmentDTO dto = new AssignmentDTO("A001", "V001", "R001", LocalDate.now());
        Mockito.when(assignmentService.updateAssignment(any(), any())).thenReturn(dto);

        mockMvc.perform(put("/assignments/A001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignmentId").value("A001"));
    }

    @Test
    void testDeleteAssignment() throws Exception {
        Mockito.doNothing().when(assignmentService).deleteAssignment("A001");

        mockMvc.perform(delete("/assignments/A001"))
                .andExpect(status().isOk())
                .andExpect(content().string("Assignment deleted successfully."));
    }
}
