package com.wastewise.vehicleservice.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdGeneratorTest {

    private final IdGenerator generator = new IdGenerator();

    @Test
    void testGeneratePickupTruckId() {
        String id = generator.generateVehicleId("Pickup Truck");
        assertTrue(id.startsWith("PT"));
    }

    @Test
    void testGenerateRouteTruckId() {
        String id = generator.generateVehicleId("Route Truck");
        assertTrue(id.startsWith("RT"));
    }

    @Test
    void testGenerateWithInvalidType() {
        assertThrows(IllegalArgumentException.class, () -> generator.generateVehicleId("InvalidType"));
    }
}