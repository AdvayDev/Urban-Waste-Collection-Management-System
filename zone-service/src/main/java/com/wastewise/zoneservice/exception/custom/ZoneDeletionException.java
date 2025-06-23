package com.wastewise.zoneservice.exception.custom;

import java.util.List;

import static com.wastewise.zoneservice.constant.ZoneConstants.ZONE_DELETION_MSG;

/**
 * Exception thrown when zone deletion is forbidden due to assigned routes.
 */
public class ZoneDeletionException extends RuntimeException {

    private final List<String> assignedRoutes;

    public ZoneDeletionException(String zoneId, List<String> assignedRoutes) {
        super(String.format(ZONE_DELETION_MSG, zoneId, assignedRoutes));
        this.assignedRoutes = assignedRoutes;
    }

    public List<String> getAssignedRoutes() {
        return assignedRoutes;
    }
}
