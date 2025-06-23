package com.wastewise.vehicleservice.utility;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class to generate unique vehicle IDs based on type.
 */
@Component
public class IdGenerator {

    private final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    public String generateVehicleId(String type) {
        String prefix = switch (type.toLowerCase()) {
            case "pickup truck" -> "PT";
            case "route truck" -> "RT";
		default -> throw new IllegalArgumentException("Unexpected value: " + type.toLowerCase());
        };

        counters.putIfAbsent(prefix, new AtomicInteger(0));
        int id = counters.get(prefix).incrementAndGet();
        return prefix + String.format("%03d", id);
    }
}
