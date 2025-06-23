package com.wastewise.vehicleservice.service;

import com.wastewise.vehicleservice.dto.VehicleDTO;
import com.wastewise.vehicleservice.entity.Vehicle;
import com.wastewise.vehicleservice.enums.VehicleStatus;
import com.wastewise.vehicleservice.enums.VehicleType;
import com.wastewise.vehicleservice.exception.ResourceNotFoundException;
import com.wastewise.vehicleservice.repository.VehicleRepository;
import com.wastewise.vehicleservice.service.impl.VehicleServiceImpl;
import com.wastewise.vehicleservice.utility.IdGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private final ModelMapper modelMapper = new ModelMapper();

    private Vehicle vehicle;
    private VehicleDTO dto;

    @BeforeEach
    void setup() {
        vehicleService = new VehicleServiceImpl(vehicleRepository, modelMapper, idGenerator);

        vehicle = Vehicle.builder()
                .vehicleId("PT001")
                .registrationNo("AP16AB1234")
                .type(VehicleType.PICKUP_TRUCK)
                .status(VehicleStatus.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        dto = VehicleDTO.builder()
                .vehicleId("PT001")
                .registrationNo("AP16AB1234")
                .type(VehicleType.PICKUP_TRUCK)
                .status(VehicleStatus.AVAILABLE)
                .build();
    }

    @Test
    void testCreateVehicle() {
        when(idGenerator.generateVehicleId("Pickup Truck")).thenReturn("PT001");
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleDTO result = vehicleService.createVehicle(dto);

        assertNotNull(result);
        assertEquals("PT001", result.getVehicleId());
    }

    @Test
    void testGetVehicleById_Success() {
        when(vehicleRepository.findById("PT001")).thenReturn(Optional.of(vehicle));
        VehicleDTO found = vehicleService.getVehicleById("PT001");

        assertEquals("AP16AB1234", found.getRegistrationNo());
    }

    @Test
    void testGetVehicleById_NotFound() {
        when(vehicleRepository.findById("X999")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> vehicleService.getVehicleById("X999"));
    }

    @Test
    void testGetAllVehicles() {
        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));
        List<VehicleDTO> list = vehicleService.getAllVehicles();

        assertEquals(1, list.size());
    }

    @Test
    void testUpdateVehicle() {
        when(vehicleRepository.findById("PT001")).thenReturn(Optional.of(vehicle));
        vehicleService.updateVehicle("PT001", dto);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void testDeleteVehicle() {
        when(vehicleRepository.findById("PT001")).thenReturn(Optional.of(vehicle));
        vehicleService.deleteVehicle("PT001");
        verify(vehicleRepository).delete(vehicle);
    }

    @Test
    void testGetVehiclesByTypeAndStatus() {
        when(vehicleRepository.findByTypeAndStatus(VehicleType.PICKUP_TRUCK, VehicleStatus.AVAILABLE))
                .thenReturn(List.of(vehicle));
        List<VehicleDTO> list = vehicleService.getVehiclesByTypeAndStatus("PICKUP_TRUCK", "AVAILABLE");

        assertEquals(1, list.size());
        assertEquals("PT001", list.get(0).getVehicleId());
    }
}
