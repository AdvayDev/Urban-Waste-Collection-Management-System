package com.wastewise.auth_service.dto;

import lombok.Data;

@Data
public class PasswordResetDTO {
    private String workerId;
    private String newPassword;
}