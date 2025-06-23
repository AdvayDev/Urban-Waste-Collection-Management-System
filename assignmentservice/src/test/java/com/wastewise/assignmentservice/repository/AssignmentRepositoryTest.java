
package com.wastewise.assignmentservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import com.wastewise.assignmentservice.config.DefaultJpaTestConfiguration;
import com.wastewise.assignmentservice.entity.Assignment;


@DataJpaTest
@ContextConfiguration(classes = DefaultJpaTestConfiguration.class)

class AssignmentRepositoryTest {

    @Autowired
    private AssignmentRepository repository;

    @Test
    void testSaveAndFindByVehicleId() {
        Assignment assignment = Assignment.builder()
                .assignmentId("A001")
                .vehicleId("V001")
                .routeId("R001")
                .dateAssigned(LocalDate.now())
                .build();
        repository.save(assignment);

        List<Assignment> result = repository.findByVehicleId("V001");
        assertFalse(result.isEmpty());
        assertEquals("R001", result.get(0).getRouteId());
    }

    @Test
    void testExistsByVehicleIdAndRouteId() {
        Assignment assignment = Assignment.builder()
                .assignmentId("A002")
                .vehicleId("V002")
                .routeId("R002")
                .dateAssigned(LocalDate.now())
                .build();
        repository.save(assignment);

        boolean exists = repository.existsByVehicleIdAndRouteId("V002", "R002");
        assertTrue(exists);
    }
}
