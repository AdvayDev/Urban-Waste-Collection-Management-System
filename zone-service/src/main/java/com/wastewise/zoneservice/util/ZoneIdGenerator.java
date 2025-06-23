package com.wastewise.zoneservice.util;

import com.wastewise.zoneservice.repository.ZoneRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class to generate unique Zone IDs in the format Z001, Z002, ...
 * Uses AtomicInteger to avoid reuse of IDs after deletion.
 */
@Component
@RequiredArgsConstructor
public class ZoneIdGenerator {

    private final ZoneRepository zoneRepository;

    // Atomic counter to hold the highest numeric ID part used so far
    private AtomicInteger counter;

    /**
     * Initialize the counter with the max existing Zone ID numeric part from DB.
     * This ensures new IDs are always incremented beyond existing ones.
     */
    @PostConstruct
    public void init() {
        // Fetch the max zoneId from DB, parse numeric part, set as initial counter
        String maxZoneId = zoneRepository.findAll().stream()
                .map(zone -> zone.getZoneId().substring(1)) // remove 'Z'
                .max(String::compareTo)
                .orElse("000");

        int maxId = Integer.parseInt(maxZoneId);
        counter = new AtomicInteger(maxId);
    }

    /**
     * Generates a new unique Zone ID.
     * @return the new Zone ID string (e.g., Z001)
     */
    public String generateZoneId() {
        int newId = counter.incrementAndGet();
        return String.format("Z%03d", newId);
    }
}
