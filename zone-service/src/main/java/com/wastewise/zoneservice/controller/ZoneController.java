package com.wastewise.zoneservice.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wastewise.zoneservice.constant.ZoneConstants;
import com.wastewise.zoneservice.dto.ZoneCreationRequestDTO;
import com.wastewise.zoneservice.dto.ZoneNameAndIdResponse;
import com.wastewise.zoneservice.dto.ZoneUpdateRequestDTO;
import com.wastewise.zoneservice.entity.Zone;
import com.wastewise.zoneservice.payload.RestResponse;
import com.wastewise.zoneservice.service.ZoneService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for Zone management APIs.
 */
@RestController
@RequestMapping(ZoneConstants.BASE_ZONE_API)
@RequiredArgsConstructor
public class ZoneController {

    private final ZoneService zoneService;
    private static final Logger logger = LoggerFactory.getLogger(ZoneController.class);

    /**
     * Create a new zone.
     *
     * @param request zone creation data
     * @return Response with created zone ID
     */
    @PostMapping("/create")
    public ResponseEntity<RestResponse<Object>> createZone(@Valid @RequestBody ZoneCreationRequestDTO request) {
        logger.info("Received request to create zone with name: {}", request.getZoneName());
        Zone created = zoneService.createZone(request);
        logger.info("Zone created successfully with ID: {}", created.getZoneId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                RestResponse.builder()
                        .message(ZoneConstants.ZONE_CREATED_MSG)
                        .data(Map.of("zoneId", created.getZoneId()))
                        .build()
        );
    }

    /**
     * Update an existing zone.
     *
     * @param zoneId  ID of zone to update
     * @param request update data
     * @return Response with updated zone details
     */
    @PutMapping("/update/{zoneId}")
    public ResponseEntity<RestResponse<Object>> updateZone(@PathVariable String zoneId,
                                                           @Valid @RequestBody ZoneUpdateRequestDTO request) {
        logger.info("Received request to update zone with ID: {}", zoneId);
        Zone updated = zoneService.updateZone(zoneId, request);
        logger.info("Zone updated successfully with ID: {}", updated.getZoneId());
        return ResponseEntity.ok(
                RestResponse.builder()
                        .message(ZoneConstants.ZONE_UPDATED_MSG)
                        .data(Map.of(
                                "zoneId", updated.getZoneId(),
                                "zoneName", updated.getZoneName(),
                                "areaCoverage", updated.getAreaCoverage()
                        ))
                        .build()
        );
    }

    /**
     * Delete a zone by ID.
     *
     * @param zoneId ID of zone to delete
     * @return Response message
     */
    @DeleteMapping("/delete/{zoneId}")
    public ResponseEntity<RestResponse<Object>> deleteZone(@PathVariable String zoneId) {
        logger.info("Received request to delete zone with ID: {}", zoneId);
        zoneService.deleteZone(zoneId);
        logger.info("Zone deleted successfully with ID: {}", zoneId);
        return ResponseEntity.ok(
                RestResponse.builder()
                        .message(ZoneConstants.ZONE_DELETED_MSG)
                        .data(null)
                        .build()
        );
    }

    /**
     * Get list of all zones.
     *
     * @return List of zones
     */
    @GetMapping("/list")
    public ResponseEntity<RestResponse<Object>> getAllZones() {
        logger.info("Received request to fetch all zones");
        List<Zone> zones = zoneService.getAllZones();
        logger.info("Returning {} zones", zones.size());
        return ResponseEntity.ok(
                RestResponse.builder()
                        .message(ZoneConstants.ZONES_LISTED_MSG)
                        .data(zones)
                        .build()
        );
    }

    /**
     * Get a zone by ID.
     *
     * @param zoneId zone ID
     * @return Zone entity
     */
    @GetMapping("/{zoneId}")
    public ResponseEntity<RestResponse<Object>> getZoneById(@PathVariable String zoneId) {
        logger.info("Received request to fetch zone with ID: {}", zoneId);
        Zone zone = zoneService.getZoneById(zoneId);
        logger.info("Returning zone with ID: {}", zoneId);
        return ResponseEntity.ok(
                RestResponse.builder()
                        .message(ZoneConstants.ZONE_RETRIEVED_MSG)
                        .data(zone)
                        .build()
        );
    }

    /**
     * Check if zone exists by ID.
     *
     * @param zoneId zone ID
     * @return true if exists, false otherwise
     */
    @GetMapping("/{zoneId}/exists")
    public ResponseEntity<RestResponse<Object>> zoneExists(@PathVariable String zoneId) {
        logger.info("Checking existence of zone with ID: {}", zoneId);
        boolean exists = zoneService.existsByZoneId(zoneId);
        return ResponseEntity.ok(
                RestResponse.builder()
                        .message(ZoneConstants.ZONE_EXISTENCE_CHECK_MSG)
                        .data(exists)
                        .build()
        );
    }
    /**
     * Get all zone IDs and names only.
     *
     * @return List of ZoneNameAndIdResponse
     */
    @GetMapping("/namesandids")
    public ResponseEntity<RestResponse<Object>> getAllZoneNamesAndIds() {
        logger.info("Received request for all zone names and IDs");
        List<ZoneNameAndIdResponse> zones = zoneService.getAllZoneNamesAndIds();
        return ResponseEntity.ok(
                RestResponse.builder()
                        .message("Zone names and IDs retrieved successfully")
                        .data(zones)
                        .build()
        );
    }
}
