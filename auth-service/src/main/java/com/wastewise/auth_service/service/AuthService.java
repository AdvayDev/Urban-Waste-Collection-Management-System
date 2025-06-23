package com.wastewise.auth_service.service;

import com.wastewise.auth_service.dto.LoginRequestDTO;
import com.wastewise.auth_service.dto.LoginResponseDTO;
import com.wastewise.auth_service.dto.PasswordResetDTO;
import com.wastewise.auth_service.dto.RegisterWorkerDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO dto);

    void registerWorker(RegisterWorkerDTO dto);

    void resetPassword(PasswordResetDTO dto);
}