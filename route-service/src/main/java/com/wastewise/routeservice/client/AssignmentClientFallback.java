package com.wastewise.routeservice.client;

import com.wastewise.routeservice.dto.AssignmentDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Fallback implementation for AssignmentClient in case the service is unavailable.
 */
@Component
public class AssignmentClientFallback implements AssignmentClient {

    @Override
    public List<AssignmentDTO> getAssignmentsByRouteId(String routeId) {
        return Collections.emptyList();
    }
}