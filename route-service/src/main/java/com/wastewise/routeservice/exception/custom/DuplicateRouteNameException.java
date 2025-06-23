package com.wastewise.routeservice.exception.custom;

import com.wastewise.routeservice.constant.RouteConstants;

/**
 * ------------------------------------------------------------------------------
 * Exception: DuplicateRouteNameException
 * ------------------------------------------------------------------------------
 * Thrown when a route with the same name already exists in the specified zone.
 * Uses RouteConstants for message formatting.
 * ------------------------------------------------------------------------------
 */
public class DuplicateRouteNameException extends RuntimeException {

    /**
     * Constructs a new exception with a detailed message from constants.
     *
     * @param routeName the duplicate route name
     * @param zoneId    the zone where duplication occurred
     */
    public DuplicateRouteNameException(String routeName, String zoneId) {
        super(String.format(RouteConstants.DUPLICATE_ROUTE_MSG, routeName, zoneId));
    }
}
