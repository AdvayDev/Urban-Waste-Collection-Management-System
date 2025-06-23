package com.wastewise.zoneservice.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wastewise.zoneservice.client.RouteClient;
import com.wastewise.zoneservice.dto.ZoneCreationRequestDTO;
import com.wastewise.zoneservice.dto.ZoneNameAndIdResponse;
import com.wastewise.zoneservice.dto.ZoneUpdateRequestDTO;
import com.wastewise.zoneservice.entity.Zone;
import com.wastewise.zoneservice.exception.custom.DuplicateZoneNameException;
import com.wastewise.zoneservice.exception.custom.NoZoneChangesDetectedException;
import com.wastewise.zoneservice.exception.custom.ZoneDeletionException;
import com.wastewise.zoneservice.exception.custom.ZoneNotFoundException;
import com.wastewise.zoneservice.repository.ZoneRepository;
import com.wastewise.zoneservice.service.ZoneService;
import com.wastewise.zoneservice.util.ZoneIdGenerator;

import lombok.RequiredArgsConstructor;

/**
 * ------------------------------------------------------------------------------
 * Service Implementation: ZoneServiceImpl
 * ------------------------------------------------------------------------------
 * Handles business logic for Zone operations.
 * Validates name uniqueness, prevents deletion with routes, and logs actions.
 * ------------------------------------------------------------------------------
 */
@Service
@RequiredArgsConstructor
public class ZoneServiceImpl implements ZoneService {

    private final ZoneRepository zoneRepository;
    private final ZoneIdGenerator idGenerator;
    private final RouteClient routeClient;
    private static final Logger logger = LoggerFactory.getLogger(ZoneServiceImpl.class);

    @Override
    @Transactional
    public Zone createZone(ZoneCreationRequestDTO request) {
        logger.info("Creating zone with name: {}", request.getZoneName());

        zoneRepository.findByZoneName(request.getZoneName())
                .ifPresent(z -> {
                    logger.error("Duplicate zone name: {}", request.getZoneName());
                    throw new DuplicateZoneNameException(request.getZoneName());
                });

        String zoneId = idGenerator.generateZoneId();

        Zone zone = Zone.builder()
                .zoneId(zoneId)
                .zoneName(request.getZoneName())
                .areaCoverage(request.getAreaCoverage())
                .build();

        Zone createdZone = zoneRepository.save(zone);
        logger.info("Zone created successfully with ID: {}", createdZone.getZoneId());
        return createdZone;
    }

    @Override
    @Transactional
    public Zone updateZone(String zoneId, ZoneUpdateRequestDTO request) {
        logger.info("Updating zone with ID: {}", zoneId);

        Zone existing = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ZoneNotFoundException(zoneId));

        boolean changed = false;

        if (!existing.getZoneName().equals(request.getZoneName())) {
            zoneRepository.findByZoneName(request.getZoneName()).ifPresent(z -> {
                throw new DuplicateZoneNameException(request.getZoneName());
            });
            existing.setZoneName(request.getZoneName());
            changed = true;
        }

        if (!existing.getAreaCoverage().equals(request.getAreaCoverage())) {
            existing.setAreaCoverage(request.getAreaCoverage());
            changed = true;
        }

        if (!changed) {
            throw new NoZoneChangesDetectedException(zoneId);
        }

        Zone updatedZone = zoneRepository.save(existing);
        logger.info("Zone updated successfully with ID: {}", updatedZone.getZoneId());
        return updatedZone;
    }

    @Override
    @Transactional
    public void deleteZone(String zoneId) {
        logger.info("Deleting zone with ID: {}", zoneId);

        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> {
                    logger.error("Zone not found with ID: {}", zoneId);
                    return new ZoneNotFoundException(zoneId);
                });

        // ✅ Fix here: extract list from RestResponse
        List<String> assignedRoutes = routeClient.getRoutesByZoneId(zoneId).getData();

        if (assignedRoutes != null && !assignedRoutes.isEmpty()) {
            logger.error("Zone deletion failed, assigned routes found: {}", assignedRoutes);
            throw new ZoneDeletionException(zoneId, assignedRoutes);
        }

        zoneRepository.delete(zone);
        logger.info("Zone deleted successfully with ID: {}", zoneId);
    }



    @Override
    public List<Zone> getAllZones() {
        logger.info("Fetching all zones");
        return zoneRepository.findAll();
    }

    @Override
    public Zone getZoneById(String zoneId) {
        logger.info("Fetching zone with ID: {}", zoneId);
        return zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ZoneNotFoundException(zoneId));
    }

    @Override
    public boolean existsByZoneId(String zoneId) {
        logger.info("Checking existence of zone with ID: {}", zoneId);
        return zoneRepository.existsByZoneId(zoneId);
    }
    
    @Override
    public List<ZoneNameAndIdResponse> getAllZoneNamesAndIds() {
        logger.info("Fetching all zone IDs and names only");
        return zoneRepository.findAll().stream()
                .map(zone -> new ZoneNameAndIdResponse(zone.getZoneId(), zone.getZoneName()))
                .toList();
    }
}
