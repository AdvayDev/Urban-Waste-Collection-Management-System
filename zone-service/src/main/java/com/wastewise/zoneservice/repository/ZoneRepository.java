package com.wastewise.zoneservice.repository;

import com.wastewise.zoneservice.entity.Zone;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for Zone entity.
 */
public interface ZoneRepository extends JpaRepository<Zone, String> {

    Optional<Zone> findByZoneName(String zoneName);

    boolean existsByZoneId(String zoneId);
}
