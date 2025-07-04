package com.wastewise.routeservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO used to receive assignment data from Assignment Service via Feign.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDTO {

    @JsonProperty("assignmentId") // Match the field name returned from Assignment Service
    private String id;

    @JsonProperty("routeId")
    private String routeId;

    @JsonProperty("workerId")
    private String workerId;
}
