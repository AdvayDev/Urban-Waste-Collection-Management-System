package com.wastewise.auth_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wastewise.auth_service.dto.LoginRequestDTO;
import com.wastewise.auth_service.dto.LoginResponseDTO;
import com.wastewise.auth_service.dto.PasswordResetDTO;
import com.wastewise.auth_service.dto.RegisterWorkerDTO;
import com.wastewise.auth_service.exception.InvalidCredentialsException;
import com.wastewise.auth_service.exception.InvalidRoleException;
import com.wastewise.auth_service.exception.ResourceNotFoundException;
import com.wastewise.auth_service.exception.WorkerAlreadyExistsException;
import com.wastewise.auth_service.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Import(AuthServiceControllerTest.TestConfig.class)
class AuthServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void loginSuccess() throws Exception {
        LoginRequestDTO req = new LoginRequestDTO("W001", "pass");
        LoginResponseDTO res = new LoginResponseDTO("token123", "ADMIN");
        Mockito.when(authService.login(any(LoginRequestDTO.class))).thenReturn(res);

        mockMvc.perform(post("/wastewise/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void loginInvalidCredentials() throws Exception {
        LoginRequestDTO req = new LoginRequestDTO("W001", "wrong");
        Mockito.when(authService.login(any())).thenThrow(new InvalidCredentialsException("Invalid password entered"));

        mockMvc.perform(post("/wastewise/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid password entered"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void registerWorkerConflict() throws Exception {
        RegisterWorkerDTO req = new RegisterWorkerDTO("W002", "WORKER");
        Mockito.doThrow(new WorkerAlreadyExistsException("already exists")).when(authService).registerWorker(any());

        mockMvc.perform(post("/wastewise/internal/register-worker")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    void resetPasswordNotFound() throws Exception {
        PasswordResetDTO req = new PasswordResetDTO("W003", "new");
        Mockito.doThrow(new ResourceNotFoundException("User not found")).when(authService).resetPassword(any());

        mockMvc.perform(post("/wastewise/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void validateTokenBadRole() throws Exception {
        Mockito.when(authService.validateToken(anyString()))
                .thenThrow(new InvalidRoleException("Invalid Role"));

        mockMvc.perform(get("/wastewise/validate")
                        .header("Authorization", "Bearer bad.token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid Role"));
    }

    @Configuration
    static class TestConfig {
        @Bean
        public AuthService authService() {
            return Mockito.mock(AuthService.class);
        }
    }
}