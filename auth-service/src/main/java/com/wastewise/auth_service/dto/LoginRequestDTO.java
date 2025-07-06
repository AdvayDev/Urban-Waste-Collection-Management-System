package com.wastewise.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequestDTO {

    @NotBlank(message = "Worker Id is required")
    @Pattern(regexp = "^W\\d{3}$", message = "Worker ID does not follow the required structure")
    private String workerId;

    @NotBlank(message = "Password is required")
    private String password;
}
