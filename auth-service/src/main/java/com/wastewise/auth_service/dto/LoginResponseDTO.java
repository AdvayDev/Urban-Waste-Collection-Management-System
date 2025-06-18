package com.wastewise.auth_service.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {

    private String token;
    private String role;
}
