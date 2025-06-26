package com.WasteWise.WasteCollectionLogs.Dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WasteLogUpdateRequestDTO {
	@NotEmpty(message = "Worker ID cannot be empty.") 
    @NotNull(message = "Worker ID cannot be null.")
	 @Pattern(regexp = "^W\\d{3}$", message = "Invalid Worker ID format. Must be like W456.")
    private String workerId;


	    @NotNull(message = "Weight Collected cannot be null.")
	    @Positive(message = "Weight Collected must be a positive value.")
	    private Double weightCollected;

}
