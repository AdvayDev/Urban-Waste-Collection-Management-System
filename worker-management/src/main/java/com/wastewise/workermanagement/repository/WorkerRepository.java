package com.wastewise.workermanagement.repository;

import com.wastewise.workermanagement.dto.WorkerInfoDTO;
import com.wastewise.workermanagement.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerRepository extends JpaRepository<Worker,String> {

    @Query("Select count(w) from Worker w")
    Long countAll();

    @Query("Select w.workerId from Worker w")
    List<String> findAllWorkerId();

    @Query("SELECT new dto.com.wastewise.workermanagement.WorkerInfoDTO(w.id, w.name) FROM Worker w WHERE w.workerStatus = 'AVAILABLE' AND w.roleId = '003'")
    List<WorkerInfoDTO> findAvailableWorkersByRole();

    boolean existsByContactNumber(String contactNumber);

    boolean existsByContactEmail(String contactEmail);

    boolean existsByContactNumberAndWorkerIdNot(String number, String id);

    boolean existsByContactEmailAndWorkerIdNot(String email, String id);
}
