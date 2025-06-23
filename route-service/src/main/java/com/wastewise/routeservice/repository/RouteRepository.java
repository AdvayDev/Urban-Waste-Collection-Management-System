package com.wastewise.routeservice.repository;

import com.wastewise.routeservice.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Route entity.
 */
public interface RouteRepository extends JpaRepository<Route, String>{
	Optional<Route> findByRouteNameAndZoneId(String routeName, String zoneId);
	
	List<Route> findByZoneId(String zoneId);
	long countByZoneId(String zoneId);

}
