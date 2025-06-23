package com.wastewise.vehicleservice.repository;

import com.wastewise.vehicleservice.entity.Vehicle;
import com.wastewise.vehicleservice.enums.VehicleStatus;
import com.wastewise.vehicleservice.enums.VehicleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    void testFindByTypeAndStatus() {
        Vehicle vehicle = Vehicle.builder()
                .vehicleId("PT001")
                .registrationNo("TS01AB1234")
                .type(VehicleType.PICKUP_TRUCK)
                .status(VehicleStatus.AVAILABLE)
                .build();

        vehicleRepository.save(vehicle);

        List<Vehicle> found = vehicleRepository.findByTypeAndStatus(VehicleType.PICKUP_TRUCK, VehicleStatus.AVAILABLE);

        assertEquals(1, found.size());
        assertEquals("TS01AB1234", found.get(0).getRegistrationNo());
    }

    @Test
    void testSaveAndFind() {
        Vehicle vehicle = Vehicle.builder()
                .vehicleId("RT001")
                .registrationNo("TS02CD5678")
                .type(VehicleType.ROUTE_TRUCK)
                .status(VehicleStatus.UNDER_MAINTENANCE)
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        assertNotNull(saved.getVehicleId());

        Vehicle fetched = vehicleRepository.findById("RT001").orElse(null);
        assertNotNull(fetched);
        assertEquals("TS02CD5678", fetched.getRegistrationNo());
    }
}
//new test comment

