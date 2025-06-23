package com.wastewise.zoneservice.exception.custom;

import static com.wastewise.zoneservice.constant.ZoneConstants.NO_CHANGES_MSG;

/**
 * Exception thrown when no changes are detected during zone update.
 */
public class NoZoneChangesDetectedException extends RuntimeException {
    public NoZoneChangesDetectedException(String zoneId) {
        super(String.format(NO_CHANGES_MSG, zoneId));
    }
}
