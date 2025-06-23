package com.wastewise.zoneservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
/**
 * DTO for zone creation request.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ZoneCreationRequestDTO {
	
	@NotBlank(message = "Zone name must not be empty")
    private String zoneName;
	
	@NotNull(message = "Area coverage must not be null")
	@Min(value = 1, message = "Area coverage must be atleast 1")
    private Long areaCoverage;
}
