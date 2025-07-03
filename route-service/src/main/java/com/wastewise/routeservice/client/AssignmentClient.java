package com.wastewise.routeservice.client;

import com.wastewise.routeservice.dto.AssignmentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "ASSIGNMENT-SERVICE",
        configuration = com.wastewise.routeservice.config.FeignClientConfig.class,
        fallback = AssignmentClientFallback.class)
public interface AssignmentClient {
    @GetMapping("/wastewise/admin/assignments/route/{routeId}")
    List<AssignmentDTO> getAssignmentsByRouteId(@PathVariable("routeId") String routeId);
}
