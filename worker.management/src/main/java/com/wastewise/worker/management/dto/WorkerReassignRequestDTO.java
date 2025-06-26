package com.wastewise.worker.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerReassignRequestDTO {

    @NotBlank(message = "oldWorkerId 1 must not be blank")
    @Pattern(regexp = "^W\\d{3}$", message = "WorkerId should follow this pattern 'W001'")
    private String oldWorkerId1;

    @NotBlank(message = "oldWorkerId 2 must not be blank")
    @Pattern(regexp = "^W\\d{3}$", message = "WorkerId should follow this pattern 'W001'")
    private String oldWorkerId2;

    @NotBlank(message = "newWorkerId 1 must not be blank")
    @Pattern(regexp = "^W\\d{3}$", message = "WorkerId should follow this pattern 'W001'")
    private String newWorkerId1;

    @NotBlank(message = "newWorkerId 2 must not be blank")
    @Pattern(regexp = "^W\\d{3}$", message = "WorkerId should follow this pattern 'W001'")
    private String newWorkerId2;
}
