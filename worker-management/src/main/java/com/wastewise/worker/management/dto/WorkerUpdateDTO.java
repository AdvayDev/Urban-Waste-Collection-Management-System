package com.wastewise.worker.management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerUpdateDTO {
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
    @Pattern(regexp = "AVAILABLE|ABSENT|OCCUPIED", message = "Please enter valid worker status")
    private String workerStatus;
}