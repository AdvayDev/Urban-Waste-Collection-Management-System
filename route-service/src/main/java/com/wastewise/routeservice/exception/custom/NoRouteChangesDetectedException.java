package com.wastewise.routeservice.exception.custom;

import static com.wastewise.routeservice.constant.RouteConstants.NO_CHANGES_MSG;

/**
 * Thrown when an update request has no actual changes to the route.
 */
public class NoRouteChangesDetectedException extends RuntimeException {
    public NoRouteChangesDetectedException(String routeId) {
        super(String.format(NO_CHANGES_MSG, routeId));
    }
}
