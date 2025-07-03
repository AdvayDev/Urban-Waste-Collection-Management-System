package com.WasteWise.WasteCollectionLogs.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.WasteWise.WasteCollectionLogs.Model.WasteLog;

@Repository
public interface WasteLogRepository extends JpaRepository<WasteLog,Long>{
    
    List<WasteLog>findByZoneIdAndCollectionStartTimeBetween(String zoneId,LocalDateTime startDate,LocalDateTime endTime);
    
    
    List<WasteLog> findByVehicleIdAndCollectionStartTimeBetween(String vehicleId, LocalDateTime startDateTime, LocalDateTime endDateTime);
        
    Optional<WasteLog> findByWorkerIdAndCollectionEndTimeIsNull(String workerId);
    
    long countByCollectionEndTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    @Query("SELECT SUM(w.weightCollected) FROM WasteLog w WHERE w.collectionEndTime BETWEEN ?1 AND ?2")
    Double sumWeightCollectedByCollectionEndTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);


}

