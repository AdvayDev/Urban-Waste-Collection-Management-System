package com.wastewise.zoneservice.constant;

/**
 * Common constant values used across Zone Service.
 */
public class ZoneConstants {

    private ZoneConstants() {}

    public static final String BASE_ZONE_API = "/wastewise/admin/zones";

    public static final String ZONE_CREATED_MSG = "New zone created with ID";
    public static final String ZONE_UPDATED_MSG = "Zone updated successfully";
    public static final String ZONE_DELETED_MSG = "Zone deleted successfully";
    public static final String ZONE_RETRIEVED_MSG = "Zone retrieved successfully";
    public static final String ZONES_LISTED_MSG = "Zones retrieved successfully";
    public static final String ZONE_EXISTENCE_CHECK_MSG = "Zone existence check completed";

    public static final String DUPLICATE_ZONE_MSG = "Zone with name '%s' already exists.";
    public static final String ZONE_NOT_FOUND_MSG = "Zone with ID %s not found.";
    public static final String NO_CHANGES_MSG = "No changes in the zone details detected for zone ID %s";
    public static final String ZONE_DELETION_MSG = "Cannot delete zone %s because it has assigned routes: %s";
}
