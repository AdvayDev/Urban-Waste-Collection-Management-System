package com.wastewise.assignmentservice.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape=JsonFormat.Shape.STRING)
public enum VehicleStatus {
    AVAILABLE,
    UNDER_MAINTENANCE,
    UNAVAILABLE
}
