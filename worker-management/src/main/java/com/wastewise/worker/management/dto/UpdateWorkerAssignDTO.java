package com.wastewise.worker.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateWorkerAssignDTO {
    @NotBlank(message = "oldWorkerId must not be blank")
    @Pattern(regexp = "^W\\d{3}$", message = "WorkerId should follow this pattern 'W001'")
    private String oldWorkerId;

    @NotBlank(message = "newWorkerId must not be blank")
    @Pattern(regexp = "^W\\d{3}$", message = "WorkerId should follow this pattern 'W001'")
    private String newWorkerId;
}
