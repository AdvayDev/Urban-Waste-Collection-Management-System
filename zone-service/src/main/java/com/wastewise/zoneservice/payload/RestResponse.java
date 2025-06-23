package com.wastewise.zoneservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic wrapper for all REST API responses.
 *
 * @param <T> the type of data being returned in the response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestResponse<T> {
    
    /**
     * Descriptive message about the operation outcome.
     * Examples: "Zone created successfully", "Zone not found"
     */
    private String message;

    /**
     * The actual data payload returned from the API.
     * This can be a DTO, a list of objects, or even a simple string/boolean.
     */
    private T data;
}
