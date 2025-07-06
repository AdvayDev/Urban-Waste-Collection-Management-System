package com.wastewise.auth_service.controller;

import com.wastewise.auth_service.dto.LoginRequestDTO;
import com.wastewise.auth_service.dto.LoginResponseDTO;
import com.wastewise.auth_service.dto.PasswordResetDTO;
import com.wastewise.auth_service.dto.RegisterWorkerDTO;
import com.wastewise.auth_service.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/wastewise")
public class AuthServiceController {

    private final AuthService authService;

    public AuthServiceController(AuthService authService){
        this.authService = authService;
    }

    /**
     * Endpoint for user login.
     * @param dto LoginRequestDTO containing username and password.
     * @return LoginResponseDTO with authentication details.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        log.info("POST - /wastewise/login - payload: {}", dto);
        LoginResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint for worker registration.
     * Only accessible by users with ADMIN role.
     * @param dto RegisterWorkerDTO containing worker details.
     * @return ResponseEntity with success message.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/internal/register-worker")
    public ResponseEntity<String> registerWorker(@RequestBody RegisterWorkerDTO dto) {
        authService.registerWorker(dto);
        log.info("POST - /wastewise/internal/register-worker - payload: {}", dto);
        return ResponseEntity.ok("Worker registered successfully in Auth DB");
    }

    /**
     * Endpoint for resetting password.
     * @param dto PasswordResetDTO containing worker ID and new password.
     * @return ResponseEntity with success message.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetDTO dto) {
        authService.resetPassword(dto);
        log.info("POST - /wastewise/reset-password - payload: {}", dto);
        return ResponseEntity.ok("Password updated successfully");
    }

    /**
     * Endpoint to validate JWT token.
     * @param authHeader Authorization header containing the Bearer token.
     * @return LoginResponseDTO with validation details.
     */
    @GetMapping("/validate")
    public ResponseEntity<LoginResponseDTO> validateToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        log.info("GET - /wastewise/validate - token: {}", token);
        LoginResponseDTO response = authService.validateToken(token);
        return ResponseEntity.ok(response);
    }
}