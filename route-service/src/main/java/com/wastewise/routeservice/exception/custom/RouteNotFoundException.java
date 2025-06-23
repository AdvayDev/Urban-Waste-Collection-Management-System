package com.wastewise.routeservice.exception.custom;

import static com.wastewise.routeservice.constant.RouteConstants.ROUTE_NOT_FOUND_MSG;

/**
 * Thrown when a route with the specified ID is not found.
 */
public class RouteNotFoundException extends RuntimeException {
    public RouteNotFoundException(String routeId) {
        super(String.format(ROUTE_NOT_FOUND_MSG, routeId));
    }
}
