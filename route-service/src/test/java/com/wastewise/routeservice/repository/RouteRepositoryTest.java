package com.wastewise.routeservice.repository;

import com.wastewise.routeservice.entity.Route;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ------------------------------------------------------------------------------
 * RouteRepositoryTest
 * ------------------------------------------------------------------------------
 * Verifies key database operations using in-memory H2 database.
 * Focus areas: save, findByRouteNameAndZoneId, findByZoneId, countByZoneId
 * ------------------------------------------------------------------------------
 */
@DataJpaTest
class RouteRepositoryTest {

    @Autowired
    private RouteRepository routeRepository;

    /**
     * Save and retrieve a route by ID.
     */
    @Test
    @DisplayName("Save and retrieve route by ID")
    void saveAndFindById() {
        Route route = Route.builder()
                .routeId("Z001-R001")
                .routeName("Route A")
                .zoneId("Z001")
                .pickupPoints("P1,P2")
                .estimatedTime(25)
                .build();

        routeRepository.save(route);

        Optional<Route> result = routeRepository.findById("Z001-R001");
        assertThat(result).isPresent();
        assertThat(result.get().getRouteName()).isEqualTo("Route A");
    }

    /**
     * findByRouteNameAndZoneId should return route if exists.
     */
    @Test
    @DisplayName("findByRouteNameAndZoneId returns correct route")
    void findByRouteNameAndZoneId_shouldReturnRoute() {
        Route route = Route.builder()
                .routeId("Z001-R002")
                .routeName("Route B")
                .zoneId("Z001")
                .pickupPoints("P3,P4")
                .estimatedTime(30)
                .build();

        routeRepository.save(route);

        Optional<Route> result = routeRepository.findByRouteNameAndZoneId("Route B", "Z001");
        assertThat(result).isPresent();
        assertThat(result.get().getRouteId()).isEqualTo("Z001-R002");
    }

    /**
     * findByRouteNameAndZoneId should return empty if not found.
     */
    @Test
    @DisplayName("findByRouteNameAndZoneId returns empty if no match")
    void findByRouteNameAndZoneId_shouldReturnEmpty() {
        Optional<Route> result = routeRepository.findByRouteNameAndZoneId("UnknownRoute", "Z999");
        assertThat(result).isNotPresent();
    }

    /**
     * findByZoneId should return all routes in the given zone.
     */
    @Test
    @DisplayName("findByZoneId returns routes for a zone")
    void findByZoneId_shouldReturnRoutes() {
        Route route1 = Route.builder()
                .routeId("Z002-R001")
                .routeName("Route X")
                .zoneId("Z002")
                .pickupPoints("P5")
                .estimatedTime(20)
                .build();

        Route route2 = Route.builder()
                .routeId("Z002-R002")
                .routeName("Route Y")
                .zoneId("Z002")
                .pickupPoints("P6,P7")
                .estimatedTime(35)
                .build();

        routeRepository.saveAll(List.of(route1, route2));

        List<Route> result = routeRepository.findByZoneId("Z002");
        assertThat(result).hasSize(2);
    }

    /**
     * countByZoneId should return correct count.
     */
    @Test
    @DisplayName("countByZoneId returns correct number of routes")
    void countByZoneId_shouldReturnCount() {
        routeRepository.save(Route.builder()
                .routeId("Z003-R001")
                .routeName("Route C")
                .zoneId("Z003")
                .pickupPoints("P8")
                .estimatedTime(15)
                .build());

        routeRepository.save(Route.builder()
                .routeId("Z003-R002")
                .routeName("Route D")
                .zoneId("Z003")
                .pickupPoints("P9")
                .estimatedTime(22)
                .build());

        long count = routeRepository.countByZoneId("Z003");
        assertThat(count).isEqualTo(2);
    }
}
