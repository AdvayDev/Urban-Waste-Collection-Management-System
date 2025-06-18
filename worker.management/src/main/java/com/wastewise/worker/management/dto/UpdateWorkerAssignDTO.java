package com.wastewise.worker.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateWorkerAssignDTO {
    @NotBlank(message = "oldWorkerId must not be blank")
    @Pattern(regexp = "^W\\d{3}$", message = "WorkerId should follow this pattern 'W001'")
    private String oldWorkerId;

    @NotBlank(message = "newWorkerId must not be blank")
    @Pattern(regexp = "^W\\d{3}$", message = "WorkerId should follow this pattern 'W001'")
    private String newWorkerId;
}
