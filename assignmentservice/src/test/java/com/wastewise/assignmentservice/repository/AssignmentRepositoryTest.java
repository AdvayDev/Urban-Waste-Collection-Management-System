
package com.wastewise.assignmentservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import com.wastewise.assignmentservice.dto.AssignmentDTO;
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
    @Test
    void testFindByRouteId() {
        Assignment assignment1 = Assignment.builder()
                .assignmentId("A003")
                .vehicleId("V003")
                .routeId("R005")
                .dateAssigned(LocalDate.now())
                .build();

        Assignment assignment2 = Assignment.builder()
                .assignmentId("A004")
                .vehicleId("V004")
                .routeId("R005")
                .dateAssigned(LocalDate.now())
                .build();

        repository.save(assignment1);
        repository.save(assignment2);

        List<Assignment> results = repository.findByRouteId("R005");

        assertEquals(2, results.size());
        assertEquals("V003", results.get(0).getVehicleId());
        assertEquals("V004", results.get(1).getVehicleId());
    }

}
