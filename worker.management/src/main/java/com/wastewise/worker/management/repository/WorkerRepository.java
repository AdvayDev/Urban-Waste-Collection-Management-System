package com.wastewise.worker.management.repository;

import com.wastewise.worker.management.model.Worker;
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

    @Query("Select w.workerId from Worker w WHERE w.workerStatus = 'available' AND w.roleId = '003'")
    List<String> findWorkerIdAvailableStatus();

    boolean existsByContactNumber(String contactNumber);

    boolean existsByContactEmail(String contactEmail);

    boolean existsByContactNumberAndWorkerIdNot(String number, String id);

    boolean existsByContactEmailAndWorkerIdNot(String email, String id);
}
