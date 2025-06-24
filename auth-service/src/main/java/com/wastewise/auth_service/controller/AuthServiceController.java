package com.wastewise.auth_service.controller;

import com.wastewise.auth_service.dto.LoginRequestDTO;
import com.wastewise.auth_service.dto.LoginResponseDTO;
import com.wastewise.auth_service.dto.PasswordResetDTO;
import com.wastewise.auth_service.dto.RegisterWorkerDTO;
import com.wastewise.auth_service.service.serviceimpl.AuthServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wastewise")
public class AuthServiceController {

    private final AuthServiceImpl authServiceImpl;

    public AuthServiceController(AuthServiceImpl authServiceImpl){
        this.authServiceImpl = authServiceImpl;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = authServiceImpl.login(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/internal/register-worker")
    public ResponseEntity<String> registerWorker(@RequestBody RegisterWorkerDTO dto) {
        authServiceImpl.registerWorker(dto);
        return ResponseEntity.ok("Worker registered successfully in Auth DB");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetDTO dto) {
        authServiceImpl.resetPassword(dto);
        return ResponseEntity.ok("Password updated successfully");
    }

    @GetMapping("/validate")
    public ResponseEntity<LoginResponseDTO> validateToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        LoginResponseDTO response = authServiceImpl.validateToken(token);
        return ResponseEntity.ok(response);
    }


}