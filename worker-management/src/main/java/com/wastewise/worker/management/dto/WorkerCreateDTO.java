package com.wastewise.worker.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerCreateDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Contact number is required")
    @Size(min = 10, max = 10, message = "Contact number must be exactly 10 digits")
    private String contactNumber;

    @Email(message = "Invalid email format")
    private String contactEmail;

    @Pattern(regexp = "002|003", message = "Role ID must be either '002' or '003'")
    private String roleId;

    @NotBlank(message = "Valid worker status is required")
    @Pattern(regexp = "AVAILABLE", message = "worker status should be available when creating")
    private String workerStatus;

}
