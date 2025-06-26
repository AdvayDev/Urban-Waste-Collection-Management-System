package com.wastewise.workermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterWorkerDTO {
    private String workerId;
    private String roleName;
}