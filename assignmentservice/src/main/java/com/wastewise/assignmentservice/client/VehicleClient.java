package com.wastewise.assignmentservice.client;

import com.wastewise.assignmentservice.config.FeignClientConfig;
import com.wastewise.assignmentservice.enums.VehicleStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "vehicleservice",configuration = FeignClientConfig.class)
public interface VehicleClient {

    @PutMapping("/wastewise/admin/vehicle-management/status/{id}")
    void updateVehicleStatus(@PathVariable("id") String id, @RequestBody VehicleStatus status);
}
