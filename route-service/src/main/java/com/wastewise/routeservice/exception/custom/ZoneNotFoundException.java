package com.wastewise.routeservice.exception.custom;

/**
 * Exception thrown when a zone is not found.
 */
public class ZoneNotFoundException extends RuntimeException {
    public ZoneNotFoundException(String zoneId) {
        super("Zone with ID '" + zoneId + "' does not exist.");
    }
}
