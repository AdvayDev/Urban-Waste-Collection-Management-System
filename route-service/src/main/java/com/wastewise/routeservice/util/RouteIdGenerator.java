package com.wastewise.routeservice.util;

import com.wastewise.routeservice.entity.Route;
import com.wastewise.routeservice.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class to generate unique Route IDs in the format Z001-R001, Z001-R002, ...
 * Uses AtomicInteger per zone to ensure IDs are sequential and not reused even after deletion.
 */
@Component
@RequiredArgsConstructor
public class RouteIdGenerator {

    private final RouteRepository routeRepository;

    // Map to store the last used route number per zone
    private final Map<String, AtomicInteger> zoneRouteCounters = new ConcurrentHashMap<>();

    /**
     * Initialize the counter for each zone based on the max existing Route ID per zone from DB.
     */
    @PostConstruct
    public void init() {
        for (Route route : routeRepository.findAll()) {
            String zoneId = route.getZoneId(); // e.g., Z001
            String routeId = route.getRouteId(); // e.g., Z001-R003

            // Extract numeric part after "R"
            String routeNumberStr = routeId.substring(routeId.indexOf("-R") + 2);
            int routeNumber = Integer.parseInt(routeNumberStr);

            zoneRouteCounters.compute(zoneId, (key, counter) -> {
                if (counter == null) return new AtomicInteger(routeNumber);
                return new AtomicInteger(Math.max(counter.get(), routeNumber));
            });
        }
    }

    /**
     * Generates a new unique Route ID for the given zone.
     *
     * @param zoneId the zone ID (e.g., Z001)
     * @return generated Route ID (e.g., Z001-R004)
     */
    public String generateRouteId(String zoneId) {
        zoneRouteCounters.putIfAbsent(zoneId, new AtomicInteger(0));
        int nextRouteNumber = zoneRouteCounters.get(zoneId).incrementAndGet();
        return String.format("%s-R%03d", zoneId, nextRouteNumber);
    }
}