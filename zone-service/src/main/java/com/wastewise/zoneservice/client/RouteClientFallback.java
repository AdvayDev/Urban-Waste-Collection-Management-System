package com.wastewise.zoneservice.client;

import com.wastewise.zoneservice.payload.RestResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Fallback implementation for RouteClient in case route service is unavailable.
 */
@Component
public class RouteClientFallback implements RouteClient {

    @Override
    public RestResponse<List<String>> getRoutesByZoneId(String zoneId) {
        // Fallback returns an empty list wrapped in RestResponse
        return RestResponse.<List<String>>builder()
                .message("Fallback: route service not available")
                .data(Collections.emptyList())
                .build();
    }
}
