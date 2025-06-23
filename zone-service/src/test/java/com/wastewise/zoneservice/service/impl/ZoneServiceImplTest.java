package com.wastewise.zoneservice.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.wastewise.zoneservice.client.RouteClient;
import com.wastewise.zoneservice.dto.ZoneCreationRequestDTO;
import com.wastewise.zoneservice.dto.ZoneUpdateRequestDTO;
import com.wastewise.zoneservice.entity.Zone;
import com.wastewise.zoneservice.exception.custom.DuplicateZoneNameException;
import com.wastewise.zoneservice.exception.custom.NoZoneChangesDetectedException;
import com.wastewise.zoneservice.exception.custom.ZoneDeletionException;
import com.wastewise.zoneservice.exception.custom.ZoneNotFoundException;
import com.wastewise.zoneservice.payload.RestResponse;
import com.wastewise.zoneservice.repository.ZoneRepository;
import com.wastewise.zoneservice.util.ZoneIdGenerator;

/**
 * Unit tests for ZoneServiceImpl with business logic coverage and exception handling.
 */
class ZoneServiceImplTest {

    @Mock private ZoneRepository zoneRepository;
    @Mock private RouteClient routeClient;
    @Mock private ZoneIdGenerator zoneIdGenerator;

    @InjectMocks private ZoneServiceImpl zoneService;

    @Captor private ArgumentCaptor<String> stringCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createZone_success() {
        ZoneCreationRequestDTO request = new ZoneCreationRequestDTO("ZoneA", 100L);
        when(zoneRepository.findByZoneName("ZoneA")).thenReturn(Optional.empty());
        when(zoneIdGenerator.generateZoneId()).thenReturn("Z001");

        Zone savedZone = Zone.builder().zoneId("Z001").zoneName("ZoneA").areaCoverage(100L).build();
        when(zoneRepository.save(any())).thenReturn(savedZone);

        Zone result = zoneService.createZone(request);
        assertThat(result.getZoneId()).isEqualTo("Z001");
    }

    @Test
    void createZone_duplicate_throwsException() {
        when(zoneRepository.findByZoneName("ZoneA")).thenReturn(Optional.of(new Zone()));

        assertThatThrownBy(() -> zoneService.createZone(new ZoneCreationRequestDTO("ZoneA", 100L)))
                .isInstanceOf(DuplicateZoneNameException.class);
    }

    @Test
    void updateZone_success() {
        Zone existing = Zone.builder().zoneId("Z001").zoneName("Old").areaCoverage(50L).build();
        when(zoneRepository.findById("Z001")).thenReturn(Optional.of(existing));
        when(zoneRepository.findByZoneName("New")).thenReturn(Optional.empty());

        Zone updatedZone = Zone.builder().zoneId("Z001").zoneName("New").areaCoverage(100L).build();
        when(zoneRepository.save(any())).thenReturn(updatedZone);

        Zone result = zoneService.updateZone("Z001", new ZoneUpdateRequestDTO("New", 100L));
        assertThat(result.getZoneName()).isEqualTo("New");
    }

    @Test
    void updateZone_noChange_throwsException() {
        Zone existing = new Zone("Z001", "ZoneX", 100L, null, null);
        when(zoneRepository.findById("Z001")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() ->
                zoneService.updateZone("Z001", new ZoneUpdateRequestDTO("ZoneX", 100L)))
                .isInstanceOf(NoZoneChangesDetectedException.class);
    }

    @Test
    void updateZone_duplicateName_throwsException() {
        Zone existing = Zone.builder().zoneId("Z001").zoneName("Old").areaCoverage(50L).build();
        when(zoneRepository.findById("Z001")).thenReturn(Optional.of(existing));
        when(zoneRepository.findByZoneName("New")).thenReturn(Optional.of(new Zone()));

        assertThatThrownBy(() ->
                zoneService.updateZone("Z001", new ZoneUpdateRequestDTO("New", 100L)))
                .isInstanceOf(DuplicateZoneNameException.class);
    }

    @Test
    void updateZone_notFound_throwsException() {
        when(zoneRepository.findById("Z999")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                zoneService.updateZone("Z999", new ZoneUpdateRequestDTO("ZoneX", 100L)))
                .isInstanceOf(ZoneNotFoundException.class);
    }

    @Test
    void deleteZone_success() {
        Zone zone = new Zone("Z001", "ZoneX", 100L, null, null);
        when(zoneRepository.findById("Z001")).thenReturn(Optional.of(zone));

        RestResponse<List<String>> mockResponse = RestResponse.<List<String>>builder()
                .message("OK")
                .data(Collections.emptyList())
                .build();
        when(routeClient.getRoutesByZoneId("Z001")).thenReturn(mockResponse);

        zoneService.deleteZone("Z001");

        verify(zoneRepository).delete(zone);
    }

    @Test
    void deleteZone_withRoutes_throwsException() {
        when(zoneRepository.findById("Z001")).thenReturn(Optional.of(new Zone()));

        RestResponse<List<String>> response = RestResponse.<List<String>>builder()
                .data(List.of("R001"))
                .message("Has routes")
                .build();
        when(routeClient.getRoutesByZoneId("Z001")).thenReturn(response);

        assertThatThrownBy(() -> zoneService.deleteZone("Z001"))
                .isInstanceOf(ZoneDeletionException.class);
    }

    @Test
    void deleteZone_notFound_throwsException() {
        when(zoneRepository.findById("Z999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> zoneService.deleteZone("Z999"))
                .isInstanceOf(ZoneNotFoundException.class);
    }

    @Test
    void getZoneById_valid() {
        Zone zone = Zone.builder().zoneId("Z001").zoneName("ZoneA").build();
        when(zoneRepository.findById("Z001")).thenReturn(Optional.of(zone));

        Zone result = zoneService.getZoneById("Z001");

        assertThat(result.getZoneName()).isEqualTo("ZoneA");
    }

    @Test
    void getZoneById_invalid_throwsException() {
        when(zoneRepository.findById("Z999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> zoneService.getZoneById("Z999"))
                .isInstanceOf(ZoneNotFoundException.class);
    }

    @Test
    void getAllZones_success() {
        Zone zone = Zone.builder().zoneId("Z001").zoneName("ZoneA").build();
        when(zoneRepository.findAll()).thenReturn(List.of(zone));

        List<Zone> result = zoneService.getAllZones();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getZoneId()).isEqualTo("Z001");
    }

    @Test
    void existsByZoneId_valid() {
        when(zoneRepository.existsByZoneId("Z001")).thenReturn(true);

        boolean result = zoneService.existsByZoneId("Z001");

        assertThat(result).isTrue();
    }

    @Test
    void existsByZoneId_invalid() {
        when(zoneRepository.existsByZoneId("Z999")).thenReturn(false);

        boolean result = zoneService.existsByZoneId("Z999");

        assertThat(result).isFalse();
    }
    /**
     * Test: getAllZoneIdsAndNames returns zoneId and zoneName only.
     */
    @Test
    void getAllZoneIdsAndNames_returnsZoneIdAndName() {
        Zone zone = Zone.builder().zoneId("Z001").zoneName("ZoneA").areaCoverage(100L).build();
        when(zoneRepository.findAll()).thenReturn(List.of(zone));

        var result = zoneService.getAllZoneNamesAndIds();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getZoneId()).isEqualTo("Z001");
        assertThat(result.get(0).getZoneName()).isEqualTo("ZoneA");
    }

}
