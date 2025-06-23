package com.wastewise.vehicleservice.service.impl;

import com.wastewise.vehicleservice.dto.VehicleDTO;
import com.wastewise.vehicleservice.entity.Vehicle;
import com.wastewise.vehicleservice.enums.VehicleStatus;
import com.wastewise.vehicleservice.enums.VehicleType;
import com.wastewise.vehicleservice.exception.ResourceNotFoundException;
import com.wastewise.vehicleservice.repository.VehicleRepository;
import com.wastewise.vehicleservice.service.VehicleService;
import com.wastewise.vehicleservice.utility.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ModelMapper modelMapper;
    private final IdGenerator idGenerator;

    @Override
    public VehicleDTO createVehicle(VehicleDTO dto) {
        Vehicle vehicle = modelMapper.map(dto, Vehicle.class);

        // Generate vehicle ID based on type
        String vehicleId = idGenerator.generateVehicleId(dto.getType().getDisplayName());
        vehicle.setVehicleId(vehicleId);
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle.setUpdatedAt(LocalDateTime.now());

        Vehicle saved = vehicleRepository.save(vehicle);
        return modelMapper.map(saved, VehicleDTO.class);
    }

    @Override
    public VehicleDTO getVehicleById(String id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));
        return modelMapper.map(vehicle, VehicleDTO.class);
    }

    @Override
    public List<VehicleDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(vehicle -> modelMapper.map(vehicle, VehicleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updateVehicle(String id, VehicleDTO dto) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));

        modelMapper.map(dto, existing); // Update fields
        existing.setUpdatedAt(LocalDateTime.now());

        vehicleRepository.save(existing);
    }

    @Override
    public void deleteVehicle(String id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));
        vehicleRepository.delete(vehicle);
    }

    @Override
    public List<VehicleDTO> getVehiclesByTypeAndStatus(String type, String status) {
        VehicleType vehicleType = VehicleType.valueOf(type);
        VehicleStatus vehicleStatus = VehicleStatus.valueOf(status);

        return vehicleRepository.findByTypeAndStatus(vehicleType, vehicleStatus).stream()
                .map(vehicle -> modelMapper.map(vehicle, VehicleDTO.class))
                .collect(Collectors.toList());
    }



}
