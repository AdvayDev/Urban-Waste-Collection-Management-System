package com.wastewise.zoneservice.exception.custom;

import static com.wastewise.zoneservice.constant.ZoneConstants.ZONE_NOT_FOUND_MSG;

/**
 * Exception thrown when a zone is not found.
 */
public class ZoneNotFoundException extends RuntimeException {
    public ZoneNotFoundException(String zoneId) {
        super(String.format(ZONE_NOT_FOUND_MSG, zoneId));
    }
}
