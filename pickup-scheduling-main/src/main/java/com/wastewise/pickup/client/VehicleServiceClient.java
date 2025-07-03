package com.wastewise.pickup.client;

import com.wastewise.pickup.configuration.FeignClientPickup;
import com.wastewise.pickup.dto.VehicleStatusUpdateDto;
import com.wastewise.pickup.model.enums.VehicleStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "VEHICLESERVICE", configuration = FeignClientPickup.class)
public interface VehicleServiceClient {

    @GetMapping("/wastewise/admin/vehicle-management/internal/exists")
    boolean checkVehicleExists(@RequestParam("id") String id);

    @PutMapping("/wastewise/admin/vehicle-management/status/{id}")
    void updateVehicleStatus(@PathVariable("id") String id, @RequestBody VehicleStatus status);
}
