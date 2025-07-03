package com.wastewise.routeservice.client;

import com.wastewise.routeservice.payload.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ------------------------------------------------------------------------------
 * Fallback for ZoneClient
 * ------------------------------------------------------------------------------
 * Returns default response when zone service is unavailable.
 * ------------------------------------------------------------------------------
 */
@Component
@Slf4j
public class ZoneClientFallback implements ZoneClient {

    /**
     * Fallback for zone existence check.
     *
     * @param zoneId the zone ID to verify
     * @return RestResponse with false if zone cannot be verified
     */
    @Override
    public RestResponse<Boolean> existsByZoneId(String zoneId) {
        log.warn("Zone service unavailable, cannot verify existence of zone ID: {}", zoneId);
        return RestResponse.<Boolean>builder()
                .message("Zone service unavailable - fallback response")
                .data(false)
                .build();
    }
}
