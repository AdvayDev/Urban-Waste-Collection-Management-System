package com.wastewise.vehicleservice.dto;

import com.wastewise.vehicleservice.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VehicleStatusDTO {
    private String vehicleId;
    private VehicleStatus status;
}
