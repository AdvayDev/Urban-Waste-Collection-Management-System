package com.wastewise.zoneservice.exception;

import com.wastewise.zoneservice.exception.custom.*;
import com.wastewise.zoneservice.payload.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler that captures and returns custom error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles ZoneNotFoundException with HTTP 404.
     */
    @ExceptionHandler(ZoneNotFoundException.class)
    public ResponseEntity<RestResponse<Object>> handleZoneNotFound(ZoneNotFoundException ex) {
        logger.error("Zone not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new RestResponse<>(ex.getMessage(), null));
    }

    /**
     * Handles DuplicateZoneNameException with HTTP 409.
     */
    @ExceptionHandler(DuplicateZoneNameException.class)
    public ResponseEntity<RestResponse<Object>> handleDuplicateZone(DuplicateZoneNameException ex) {
        logger.warn("Duplicate zone name: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new RestResponse<>(ex.getMessage(), null));
    }

    /**
     * Handles NoZoneChangesDetectedException with HTTP 409.
     */
    @ExceptionHandler(NoZoneChangesDetectedException.class)
    public ResponseEntity<RestResponse<Object>> handleNoChanges(NoZoneChangesDetectedException ex) {
        logger.info("No zone changes: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new RestResponse<>(ex.getMessage(), null));
    }

    /**
     * Handles ZoneDeletionException with HTTP 400.
     */
    @ExceptionHandler(ZoneDeletionException.class)
    public ResponseEntity<RestResponse<Object>> handleZoneDeletion(ZoneDeletionException ex) {
        logger.error("Zone deletion error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new RestResponse<>(ex.getMessage(), ex.getAssignedRoutes()));
    }

    /**
     * Fallback handler for all other uncaught exceptions with HTTP 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse<Object>> handleUnexpected(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RestResponse<>("Internal server error: " + ex.getMessage(), null));
    }
}
