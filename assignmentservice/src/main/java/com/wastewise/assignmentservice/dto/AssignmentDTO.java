package com.wastewise.assignmentservice.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentDTO {
    private String assignmentId;
    private String vehicleId;
    private String routeId;
    private LocalDate dateAssigned;
    //private String dateAssigned;

}
