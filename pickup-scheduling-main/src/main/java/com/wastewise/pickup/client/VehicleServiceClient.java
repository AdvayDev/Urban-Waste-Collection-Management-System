package com.wastewise.pickup.client;

import com.wastewise.pickup.dto.VehicleDto;
import com.wastewise.pickup.dto.VehicleStatusUpdateDto;
import com.wastewise.pickup.model.enums.VehicleStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "vehicle-service", url="/wastewise/admin/vehicle-management")
public interface VehicleServiceClient {
    @GetMapping("/exists/{id}")
    boolean checkVehicleExists(@PathVariable String id);

//    @GetMapping("/vehicles/{id}")
//    VehicleDto getVehicleById(@PathVariable("id") String id);

    @PutMapping("/status/{id}")
    void updateVehicleStatus(@PathVariable String id, @RequestBody VehicleStatus status);
}