package com.wastewise.routeservice.exception.custom;

import java.util.List;

import com.wastewise.routeservice.constant.RouteConstants;

/**
 * Exception thrown when trying to delete a route that has assigned tasks.
 */
public class RouteDeletionException extends RuntimeException {
    public RouteDeletionException(String routeId, List<String> assignmentIds) {
        super(String.format(RouteConstants.ROUTE_DELETION_FAILED_DUE_TO_ASSIGNMENTS, routeId, assignmentIds));
    }
}
