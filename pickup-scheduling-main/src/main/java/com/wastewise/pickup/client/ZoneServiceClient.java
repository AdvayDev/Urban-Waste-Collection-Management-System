package com.wastewise.pickup.client;

import com.wastewise.pickup.configuration.FeignClientPickup;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ZONE-SERVICE",configuration = FeignClientPickup.class)
public interface ZoneServiceClient {
//    @GetMapping("/zones")
//    List<ZoneDto> getAllZones();

    @GetMapping("/wastewise/admin/zones/internal/exists")
    boolean checkZoneExists(@RequestParam("zoneId") String zoneId);
}