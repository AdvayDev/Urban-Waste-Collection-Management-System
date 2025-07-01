package com.wastewise.pickup.client;

import com.wastewise.pickup.dto.ZoneDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "zone-service", url="/wastewise/admin/zones")
public interface ZoneServiceClient {
//    @GetMapping("/zones")
//    List<ZoneDto> getAllZones();

    @GetMapping("/{zoneId}/exists")
    boolean checkZoneExists(@PathVariable String zoneId);
}