package com.wastewise.vehicleservice.controller;

import com.wastewise.vehicleservice.dto.VehicleDTO;
import com.wastewise.vehicleservice.enums.VehicleType;
import com.wastewise.vehicleservice.enums.VehicleStatus;
import com.wastewise.vehicleservice.service.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing vehicle management.
 */
@RestController
@RequestMapping("/wastewise/admin/vehicle-management")
@Slf4j
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    /**
     * Creates a new vehicle.
     *
     * @param dto the vehicle data
     * @return the created vehicle with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<VehicleDTO> createVehicle(@RequestBody VehicleDTO dto) {
        log.info("POST /wastewise/admin/vehicle-assignments - Creating vehicle");
        VehicleDTO created = vehicleService.createVehicle(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Retrieves a vehicle by its ID.
     *
     * @param id the vehicle ID
     * @return the vehicle data with HTTP 200 status
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehicleDTO> getVehicleById(@PathVariable String id) {
        log.info("GET /wastewise/admin/vehicle-assignments/{}", id);
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    /**
     * Retrieves all vehicles.
     *
     * @return list of all vehicles with HTTP 200 status
     */
    @GetMapping
    public ResponseEntity<List<VehicleDTO>> getAllVehicles() {
        log.info("GET /wastewise/admin/vehicle-assignments");
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    /**
     * Updates an existing vehicle.
     *
     * @param id  the vehicle ID
     * @param dto the updated vehicle data
     * @return success message with HTTP 200 status
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateVehicle(@PathVariable String id, @RequestBody VehicleDTO dto) {
        log.info("PUT /wastewise/admin/vehicle-assignments/{}", id);
        vehicleService.updateVehicle(id, dto);
        return ResponseEntity.ok("Vehicle updated successfully.");
    }

    /**
     * Deletes a vehicle by ID.
     *
     * @param id the vehicle ID
     * @return HTTP 204 No Content if deletion is successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable String id) {
        log.info("DELETE /wastewise/admin/vehicle-assignments/{}", id);
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok("Vehicle deleted successfully.");
    }

    /**
     * Retrieves all available pickup trucks.
     *
     * @return list of available pickup trucks with HTTP 200 status
     */
    @GetMapping("/filter/pickuptruck")
    public ResponseEntity<List<VehicleDTO>> getAvailablePickupTrucks() {
        log.info("GET /wastewise/admin/vehicle-assignments/filter/pickuptruck");
        return ResponseEntity.ok(vehicleService.getVehiclesByTypeAndStatus(
                VehicleType.PICKUP_TRUCK.name(), VehicleStatus.AVAILABLE.name()));
    }

    /**
     * Retrieves all available route trucks.
     *
     * @return list of available route trucks with HTTP 200 status
     */
    @GetMapping("/filter/routetruck")
    public ResponseEntity<List<VehicleDTO>> getAvailableRouteTrucks() {
        log.info("GET /wastewise/admin/vehicle-assignments/filter/routetruck");
        return ResponseEntity.ok(vehicleService.getVehiclesByTypeAndStatus(
                VehicleType.ROUTE_TRUCK.name(), VehicleStatus.AVAILABLE.name()));
    }
}
