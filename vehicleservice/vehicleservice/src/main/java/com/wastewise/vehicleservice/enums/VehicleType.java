package com.wastewise.vehicleservice.enums;

/**
 * Enum representing types of vehicles used in the waste collection system.
 */
public enum VehicleType {
    PICKUP_TRUCK("Pickup Truck"),
    ROUTE_TRUCK("Route Truck");

    private final String displayName;

    VehicleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
