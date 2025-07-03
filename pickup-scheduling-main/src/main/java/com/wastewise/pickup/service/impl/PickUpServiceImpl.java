package com.wastewise.pickup.service.impl;

import com.wastewise.pickup.client.VehicleServiceClient;
import com.wastewise.pickup.client.WorkerServiceClient;
import com.wastewise.pickup.client.ZoneServiceClient;
import com.wastewise.pickup.dto.*;
import com.wastewise.pickup.exception.InvalidPickUpRequestException;
import com.wastewise.pickup.exception.PickUpNotFoundException;
import com.wastewise.pickup.model.PickUp;
import com.wastewise.pickup.model.enums.PickUpStatus;
import com.wastewise.pickup.model.enums.VehicleStatus;
import com.wastewise.pickup.model.enums.WorkerStatus;
import com.wastewise.pickup.repository.PickUpRepository;
import com.wastewise.pickup.service.PickUpService;
import com.wastewise.pickup.utility.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This service handles the creation and deletion of PickUp jobs, including validations
 * and interactions with external services for zones, vehicles, and workers.
 */
@Service
@Slf4j
public class PickUpServiceImpl implements PickUpService {

    private final PickUpRepository repository;
    private final IdGenerator idGenerator;

    private final ZoneServiceClient zoneServiceClient;
    private final VehicleServiceClient vehicleServiceClient;
    private final WorkerServiceClient workerServiceClient;

//    private final WebClient webClient;
//    private final String zoneServiceUrl;
//    private final String vehicleServiceUrl;
//    private final String workerServiceUrl;
    /**
     * Constructor for PickUpServiceImplV1.
     */
    public PickUpServiceImpl(
            PickUpRepository repository,
            IdGenerator idGenerator,
            ZoneServiceClient zoneServiceClient,
            VehicleServiceClient vehicleServiceClient,
            WorkerServiceClient workerServiceClient) {
        this.repository = repository;
        this.idGenerator = idGenerator;
        this.zoneServiceClient = zoneServiceClient;
        this.vehicleServiceClient = vehicleServiceClient;
        this.workerServiceClient = workerServiceClient;
    }

    /**
     * Creates a new PickUp.
     */
    @Override
    @Transactional
    public String createPickUp(CreatePickUpDto dto) {
        log.info("Creating pickup: {}", dto);

        // Validations for the PickUp creation request.

        // 1) Validate time slot
        if (dto.getTimeSlotEnd().isBefore(dto.getTimeSlotStart().plusMinutes(30))) {
            throw new InvalidPickUpRequestException("Invalid time slot: end time must be at least 30 minutes after start time.");
        }

        // 2) Validate zone - check if the zone exists
        Boolean zoneExists = zoneServiceClient.checkZoneExists(dto.getZoneId());
        if (zoneExists == null || !zoneExists) {
            throw new InvalidPickUpRequestException("Zone not found");
        }

        // 3) Validate vehicle - check if the vehicle exists and is available
        Boolean vehicleExists = vehicleServiceClient.checkVehicleExists(dto.getVehicleId());
        if (vehicleExists == null || !vehicleExists) {
            throw new InvalidPickUpRequestException("Vehicles are not available");
        }

        // 4) Validate workers - check if at least two valid workers exist
        long validWorkerCount = dto.getWorkerIds().stream()
                .map(workerServiceClient::checkWorkerExists)
                .filter(Boolean.TRUE::equals)
                .count();
        if (validWorkerCount < 2) {
            throw new InvalidPickUpRequestException("At least two valid workers must exist.");
        }

        // Generate ID and save PickUp
        String pickupId = idGenerator.generatePickUpId();
        PickUp pickup = PickUp.builder()
                .id(pickupId)
                .zoneId(dto.getZoneId())
                .timeSlotStart(dto.getTimeSlotStart())
                .timeSlotEnd(dto.getTimeSlotEnd())
                .frequency(dto.getFrequency())
                .locationName(dto.getLocationName())
                .vehicleId(dto.getVehicleId())
                .worker1Id(dto.getWorker1Id())
                .worker2Id(dto.getWorker2Id())
                .status(PickUpStatus.SCHEDULED)
                .build();
        repository.save(pickup);

        log.info("Pickup created with ID: {}", pickupId);

        // Notify Worker and Vehicle Services
        notifyWorkerService(dto.getWorker1Id(), WorkerStatus.OCCUPIED);
        log.info("Worker 1 {} marked as OCCUPIED", dto.getWorker1Id());

        notifyWorkerService(dto.getWorker2Id(), WorkerStatus.OCCUPIED);
        log.info("Worker 2 {} marked as OCCUPIED", dto.getWorker2Id());

        notifyVehicleService(dto.getVehicleId(), VehicleStatus.UNAVAILABLE);
        log.info("Vehicle {} marked as OCCUPIED", dto.getVehicleId());

        return pickupId;
    }

    /**
     * Deletes an existing PickUp.
     */
    @Override
    @Transactional
    public DeletePickUpResponseDto deletePickUp(String pickUpId) {
        log.info("Deleting PickUp with ID: {}", pickUpId);

        PickUp pickup = repository.findById(pickUpId)
                .orElseThrow(() -> new PickUpNotFoundException("Pickup not found with ID: " + pickUpId));
        repository.delete(pickup);

        log.info("Pickup deleted with ID: {}", pickUpId);

        // Notify Worker and Vehicle Services to set assets "AVAILABLE"
        notifyWorkerService(pickup.getWorker1Id(), WorkerStatus.AVAILABLE);
        log.info("Worker 1 {} marked as AVAILABLE", pickup.getWorker1Id());

        notifyWorkerService(pickup.getWorker2Id(), WorkerStatus.AVAILABLE);
        log.info("Worker 2 {} marked as AVAILABLE", pickup.getWorker2Id());

        notifyVehicleService(pickup.getVehicleId(), VehicleStatus.AVAILABLE);
        log.info("Vehicle {} marked as AVAILABLE", pickup.getVehicleId());

        return new DeletePickUpResponseDto(pickUpId, "DELETED");
    }

    /**
     * Notifies the Worker Service.
     */
    private void notifyWorkerService(String workerId, WorkerStatus status) {
        try {
            log.info("Notifying worker {} with status: {}", workerId, status);

            WorkerStatusUpdateDto dto = new WorkerStatusUpdateDto(workerId, status);
            workerServiceClient.changeWorkerStatus(dto);

            log.info("Worker {} successfully notified with status: {}", workerId, status);

        } catch (Exception ex) {
            log.error("Error notifying worker {}: {}", workerId, ex.getMessage(), ex);
            throw new InvalidPickUpRequestException(
                    String.format("Failed to notify worker '%s' with status '%s'", workerId, status));
        }
    }

    /**
     * Notifies the Vehicle Service.
     */
    private void notifyVehicleService(String vehicleId, VehicleStatus status) {
        try {
            log.info("Notifying vehicle {} with status: {}", vehicleId, status);

            VehicleStatusUpdateDto dto = new VehicleStatusUpdateDto(vehicleId,status);
            vehicleServiceClient.updateVehicleStatus(dto);

            log.info("Vehicle {} successfully notified with status: {}", vehicleId, status);

        } catch (Exception ex) {
            log.error("Error notifying vehicle {}: {}", vehicleId, ex.getMessage(), ex);
            throw new InvalidPickUpRequestException(
                    String.format("Failed to notify vehicle '%s' with status '%s'", vehicleId, status));
        }
    }

    public List<PickUpDto> listAllPickUps() {
        log.info("Fetching all PickUps");

        // Fetch all PickUp records and map them to DTOs
        return repository.findAll().stream()
                .map(this::mapToPickUpDto)
                .collect(Collectors.toList());
    }

    @Override
    public PickUpDto getPickUpById(String pickUpId) {
        log.info("Fetching PickUp with ID: {}", pickUpId);

        // Find the PickUp by its ID
        PickUp pickUp = repository.findById(pickUpId)
                .orElseThrow(() -> new PickUpNotFoundException("PickUp not found with ID: " + pickUpId));

        // Map the PickUp to a DTO and return it
        return mapToPickUpDto(pickUp);
    }

    private PickUpDto mapToPickUpDto(PickUp pickUp) {
        PickUpDto dto = new PickUpDto();
        dto.setId(pickUp.getId());
        dto.setZoneId(pickUp.getZoneId());
        dto.setTimeSlotStart(pickUp.getTimeSlotStart());
        dto.setTimeSlotEnd(pickUp.getTimeSlotEnd());
        dto.setFrequency(pickUp.getFrequency());
        dto.setLocationName(pickUp.getLocationName());
        dto.setVehicleId(pickUp.getVehicleId());
        dto.setWorker1Id(pickUp.getWorker1Id());
        dto.setWorker2Id(pickUp.getWorker2Id());
        dto.setStatus(pickUp.getStatus());
        return dto;
    }
    public String updatePickup(String id, CreatePickUpDto dto){
        PickUp pickup = repository.findById(id)
                .orElseThrow(() -> new PickUpNotFoundException ("PickUp not found with ID: " + id));

        pickup.setZoneId(dto.getZoneId());
        pickup.setVehicleId(dto.getVehicleId());
        pickup.setLocationName(dto.getLocationName());
        pickup.setFrequency(dto.getFrequency());
        pickup.setTimeSlotEnd(dto.getTimeSlotEnd());
        pickup.setTimeSlotStart(dto.getTimeSlotStart());
        pickup.setWorker1Id(dto.getWorker1Id());
        pickup.setWorker2Id(dto.getWorker2Id());

        repository.save(pickup);
        log.info("Pickup with id {} updated successfully",id);
        return "Updated pickup with Id "+ id;

    }
}

/**
 * Improvements and Notes:
 *
 * The WebClient is used to make synchronous calls to these services. Asynchronous handling can be implemented if needed.
 *
 * Wrap Asynchronous Calls With Retry Logic (spring-retry) for better resilience.
 *
 * Error handling can be provided for notifications to external services.
 *
 * The code is designed to be used in a microservices architecture where each service is responsible for its own domain logic.
 *
 */
