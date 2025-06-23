package com.wastewise.vehicleservice.repository;

import com.wastewise.vehicleservice.entity.Vehicle;
import com.wastewise.vehicleservice.enums.VehicleType;
import com.wastewise.vehicleservice.enums.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for vehicle data operations.
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    /**
     * Finds vehicles by type and status.
     *
     * @param type   the type of the vehicle (enum)
     * @param status the status of the vehicle (enum)
     * @return list of matching vehicles
     */
    List<Vehicle> findByTypeAndStatus(VehicleType type, VehicleStatus status);
}
