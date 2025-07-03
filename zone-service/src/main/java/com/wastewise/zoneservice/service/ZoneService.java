package com.wastewise.zoneservice.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wastewise.zoneservice.dto.ZoneCreationRequestDTO;
import com.wastewise.zoneservice.dto.ZoneNameAndIdResponse;
import com.wastewise.zoneservice.dto.ZoneUpdateRequestDTO;
import com.wastewise.zoneservice.entity.Zone;

/**
 * Service interface for Zone management.
 */
public interface ZoneService {

    /**
     * Creates a new zone based on the request data.
     *
     * @param request DTO containing zone details
     * @return the created Zone entity
     */
    Zone createZone(ZoneCreationRequestDTO request);

    /**
     * Updates an existing zone with new data.
     *
     * @param zoneId  the ID of the zone to update
     * @param request DTO with updated zone information
     * @return the updated Zone entity
     */
    Zone updateZone(String zoneId, ZoneUpdateRequestDTO request);

    /**
     * Deletes a zone by ID.
     *
     * @param zoneId the ID of the zone to delete
     */
    void deleteZone(String zoneId);

    /**
     * Retrieves all zones (non-paginated).
     *
     * @return list of all zones
     */
    List<Zone> getAllZones();

    /**
     * Retrieves paginated zones.
     *
     * @param pageable pagination details (page number, size, sort)
     * @return paginated list of zones
     */
    Page<Zone> getAllZones(Pageable pageable);

    /**
     * Retrieves a zone by its ID.
     *
     * @param zoneId the ID of the zone
     * @return the Zone entity
     */
    Zone getZoneById(String zoneId);

    /**
     * Checks if a zone exists by ID.
     *
     * @param zoneId the ID of the zone
     * @return true if the zone exists, false otherwise
     */
    boolean existsByZoneId(String zoneId);

    /**
     * Retrieves zone names and IDs only.
     *
     * @return list of ZoneNameAndIdResponse
     */
    List<ZoneNameAndIdResponse> getAllZoneNamesAndIds();
}
