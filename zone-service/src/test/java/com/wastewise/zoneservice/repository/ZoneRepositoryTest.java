package com.wastewise.zoneservice.repository;

import com.wastewise.zoneservice.entity.Zone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * -----------------------------------------------------------------------------
 * ZoneRepositoryTest
 * -----------------------------------------------------------------------------
 * Verifies core database operations using an in-memory H2 database.
 * Focuses on: save, findByZoneName, existsByZoneId
 * -----------------------------------------------------------------------------
 */
@DataJpaTest
class ZoneRepositoryTest {

    @Autowired
    private ZoneRepository zoneRepository;

    /**
     * Save a new zone and retrieve it by ID.
     */
    @Test
    @DisplayName("Save and retrieve zone by ID")
    void saveAndFindById() {
        Zone zone = Zone.builder()
                .zoneId("Z100")
                .zoneName("Zone Alpha")
                .areaCoverage(120L)
                .build();

        zoneRepository.save(zone);

        Optional<Zone> result = zoneRepository.findById("Z100");
        assertThat(result).isPresent();
        assertThat(result.get().getZoneName()).isEqualTo("Zone Alpha");
    }

    /**
     * Check if a zone exists by zone ID (should return true).
     */
    @Test
    @DisplayName("existsByZoneId returns true if zone exists")
    void existsByZoneId_shouldReturnTrue() {
        Zone zone = Zone.builder()
                .zoneId("Z101")
                .zoneName("Zone Beta")
                .areaCoverage(150L)
                .build();

        zoneRepository.save(zone);

        boolean exists = zoneRepository.existsByZoneId("Z101");
        assertThat(exists).isTrue();
    }

    /**
     * Check if a zone exists by zone ID (should return false).
     */
    @Test
    @DisplayName("existsByZoneId returns false if zone doesn't exist")
    void existsByZoneId_shouldReturnFalse() {
        boolean exists = zoneRepository.existsByZoneId("Z999");
        assertThat(exists).isFalse();
    }

    /**
     * Retrieve zone by zone name.
     */
    @Test
    @DisplayName("findByZoneName returns correct zone")
    void findByZoneName_shouldReturnZone() {
        Zone zone = Zone.builder()
                .zoneId("Z102")
                .zoneName("Zone Gamma")
                .areaCoverage(200L)
                .build();

        zoneRepository.save(zone);

        Optional<Zone> result = zoneRepository.findByZoneName("Zone Gamma");
        assertThat(result).isPresent();
        assertThat(result.get().getZoneId()).isEqualTo("Z102");
    }

    /**
     * Check if findByZoneName returns empty for non-existent name.
     */
    @Test
    @DisplayName("findByZoneName returns empty for unknown zone name")
    void findByZoneName_shouldReturnEmpty() {
        Optional<Zone> result = zoneRepository.findByZoneName("UnknownZone");
        assertThat(result).isNotPresent();
    }
}
