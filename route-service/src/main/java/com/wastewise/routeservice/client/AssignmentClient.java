package com.wastewise.routeservice.client;

import com.wastewise.routeservice.payload.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ASSIGNMENT-SERVICE", fallback = AssignmentClientFallback.class)
public interface AssignmentClient {
    @GetMapping("/wastewise/assignments/route/{routeId}")
    RestResponse<List<String>> getAssignmentIdsByRouteId(@PathVariable("routeId") String routeId);
}
