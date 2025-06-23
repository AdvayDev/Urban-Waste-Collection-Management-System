package com.wastewise.zoneservice.exception.custom;

import static com.wastewise.zoneservice.constant.ZoneConstants.DUPLICATE_ZONE_MSG;

/**
 * Exception thrown when a zone name already exists.
 */
public class DuplicateZoneNameException extends RuntimeException {
    public DuplicateZoneNameException(String zoneName) {
        super(String.format(DUPLICATE_ZONE_MSG, zoneName));
    }
}
