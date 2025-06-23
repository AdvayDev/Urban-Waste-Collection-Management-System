package com.wastewise.zoneservice.service;

import java.util.List;

import com.wastewise.zoneservice.dto.ZoneCreationRequestDTO;
import com.wastewise.zoneservice.dto.ZoneNameAndIdResponse;
import com.wastewise.zoneservice.dto.ZoneUpdateRequestDTO;
import com.wastewise.zoneservice.entity.Zone;

/**
 * Service interface for Zone management.
 */
public interface ZoneService {

    Zone createZone(ZoneCreationRequestDTO request);

    Zone updateZone(String zoneId, ZoneUpdateRequestDTO request);

    void deleteZone(String zoneId);

    List<Zone> getAllZones();

    Zone getZoneById(String zoneId);

    boolean existsByZoneId(String zoneId);
    
    List<ZoneNameAndIdResponse> getAllZoneNamesAndIds();
}
