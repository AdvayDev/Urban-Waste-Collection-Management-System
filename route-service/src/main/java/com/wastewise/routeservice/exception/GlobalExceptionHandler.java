package com.wastewise.routeservice.exception;

import com.wastewise.routeservice.exception.custom.*;
import com.wastewise.routeservice.payload.RestResponse;
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
     * Handles RouteNotFoundException with HTTP 404.
     */
    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<RestResponse<Object>> handleRouteNotFound(RouteNotFoundException ex) {
        logger.error("Route not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new RestResponse<>(ex.getMessage(), null));
    }

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
     * Handles DuplicateRouteNameException with HTTP 409.
     */
    @ExceptionHandler(DuplicateRouteNameException.class)
    public ResponseEntity<RestResponse<Object>> handleDuplicateRoute(DuplicateRouteNameException ex) {
        logger.warn("Duplicate route name: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new RestResponse<>(ex.getMessage(), null));
    }

    /**
     * Handles NoRouteChangesDetectedException with HTTP 409.
     */
    @ExceptionHandler(NoRouteChangesDetectedException.class)
    public ResponseEntity<RestResponse<Object>> handleNoChanges(NoRouteChangesDetectedException ex) {
        logger.info("No route changes: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new RestResponse<>(ex.getMessage(), null));
    }

    /**
     * Handles InvalidRouteDetailsException with HTTP 400.
     */
    @ExceptionHandler(InvalidRouteDetailsException.class)
    public ResponseEntity<RestResponse<Object>> handleInvalidDetails(InvalidRouteDetailsException ex) {
        logger.warn("Invalid route details: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new RestResponse<>(ex.getMessage(), null));
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
