package com.wastewise.routeservice.exception.custom;

import static com.wastewise.routeservice.constant.RouteConstants.INVALID_ROUTE_DETAILS_MSG;

/**
 * Exception thrown when route details are invalid.
 */
public class InvalidRouteDetailsException extends RuntimeException {
    public InvalidRouteDetailsException(String message) {
        super(String.format(INVALID_ROUTE_DETAILS_MSG, message));
    }
}
