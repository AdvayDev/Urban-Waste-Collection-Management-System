package com.wastewise.routeservice.feign;

import com.wastewise.routeservice.payload.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communicating with the Zone Service.
 */
@FeignClient(name = "zone-service")
public interface ZoneClient {

    /**
     * Checks if a zone exists by its ID.
     *
     * @param zoneId the zone ID
     * @return true if the zone exists, false otherwise
     */
    @GetMapping("/wastewise/admin/zones/{zoneId}/exists")
    RestResponse<Boolean> existsByZoneId(@PathVariable("zoneId") String zoneId);
}
