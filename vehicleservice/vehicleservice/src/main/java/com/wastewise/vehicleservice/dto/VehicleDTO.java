package com.wastewise.vehicleservice.dto;

import com.wastewise.vehicleservice.enums.VehicleStatus;
import com.wastewise.vehicleservice.enums.VehicleType;
import lombok.*;

/**
 * Data Transfer Object for Vehicle entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDTO {

    /**
     * Unique identifier for the vehicle.
     */
    private String vehicleId;

    /**
     * Registration number of the vehicle.
     */
    private String registrationNo;

    /**
     * Type of the vehicle (e.g., Pickup Truck, Route Truck).
     */
    private VehicleType type;

    /**
     * Current status of the vehicle (e.g., Available, Under Maintenance).
     */
    private VehicleStatus status;
}
