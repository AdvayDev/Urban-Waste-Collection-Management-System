package com.wastewise.routeservice.constant;

/**
 * Centralized constants used throughout the Route Service application.
 */
public class RouteConstants {

    private RouteConstants() {
        // Prevent instantiation
    }

    // Base API endpoint
    public static final String BASE_ROUTE_API = "/wastewise/admin/routes";

    // Success messages
    public static final String ROUTE_CREATED_MSG = "New route created with ID";
    public static final String ROUTE_UPDATED_MSG = "Route updated successfully";
    public static final String ROUTE_DELETED_MSG = "Route deleted successfully";
    public static final String ROUTE_RETRIEVED_MSG = "Route retrieved successfully";
    public static final String ROUTES_LISTED_MSG = "Routes retrieved successfully";

    // Validation and error messages
    public static final String ROUTE_NOT_FOUND_MSG = "Route with ID %s not found.";
    public static final String DUPLICATE_ROUTE_MSG = "Route with name '%s' already exists in this zone '%s'.";
    public static final String NO_CHANGES_MSG = "No changes detected for route with ID %s";
    public static final String INVALID_ROUTE_DETAILS_MSG = "Invalid route details: %s";
    public static final String ZONE_NOT_FOUND_MSG = "Zone with ID '%s' does not exist.";
}
