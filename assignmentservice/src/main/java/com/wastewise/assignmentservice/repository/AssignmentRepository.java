package com.wastewise.assignmentservice.repository;

import com.wastewise.assignmentservice.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, String> {
    List<Assignment> findByVehicleId(String vehicleId);
    List<Assignment> findByRouteId(String routeId);
    boolean existsByVehicleIdAndRouteId(String vehicleId, String routeId);

}
