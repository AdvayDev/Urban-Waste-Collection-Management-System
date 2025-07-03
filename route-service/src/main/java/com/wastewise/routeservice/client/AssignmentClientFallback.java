package com.wastewise.routeservice.client;

import com.wastewise.routeservice.payload.RestResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AssignmentClientFallback implements AssignmentClient {
    @Override
    public RestResponse<List<String>> getAssignmentIdsByRouteId(String routeId) {
        return RestResponse.<List<String>>builder()
                .message("Assignment service unavailable - fallback used")
                .data(Collections.emptyList())
                .build();
    }
}