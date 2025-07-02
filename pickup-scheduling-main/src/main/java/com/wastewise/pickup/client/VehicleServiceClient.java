package com.wastewise.pickup.client;

import com.wastewise.pickup.dto.VehicleStatusUpdateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "vehicle-service", url="/wastewise/admin/vehicle-management")
public interface VehicleServiceClient {
    @GetMapping("/internal/exists")
    boolean checkVehicleExists(@RequestBody String id);

//    @GetMapping("/vehicles/{id}")
//    VehicleDto getVehicleById(@PathVariable("id") String id);

    @PutMapping("/internal/status")
    void updateVehicleStatus(@RequestBody VehicleStatusUpdateDto dto);
}