package com.wastewise.vehicleservice.service;

import com.wastewise.vehicleservice.dto.VehicleDTO;

import java.util.List;

/**
 * Service interface for managing vehicle operations.
 */
public interface VehicleService {
    VehicleDTO createVehicle(VehicleDTO dto);
    VehicleDTO getVehicleById(String id);
    List<VehicleDTO> getAllVehicles();
    void updateVehicle(String id, VehicleDTO dto);
    void deleteVehicle(String id);
    List<VehicleDTO> getVehiclesByTypeAndStatus(String type, String status);
}
