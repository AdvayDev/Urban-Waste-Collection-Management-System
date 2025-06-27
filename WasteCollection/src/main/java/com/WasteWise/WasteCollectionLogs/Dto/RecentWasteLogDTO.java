package com.WasteWise.WasteCollectionLogs.Dto;
import com.WasteWise.WasteCollectionLogs.Model.WasteLog;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RecentWasteLogDTO {

    private String zoneId;
    private String vehicleId;
    private LocalDateTime collectionStartTime;
    private LocalDateTime collectionEndTime; // Will be null if in progress
    private Double weightCollected; // Will be null if in progress
    private String status; // "In Progress" or "Completed"

    public RecentWasteLogDTO(WasteLog wasteLog) {
        this.zoneId = wasteLog.getZoneId();
        this.vehicleId = wasteLog.getVehicleId();
        this.collectionStartTime = wasteLog.getCollectionStartTime();

        // Determine status and set collectionEndTime and weightCollected accordingly
        if (wasteLog.getCollectionEndTime() == null) {
            this.status = "In Progress";
            this.collectionEndTime = null;    // Explicitly set to null if in progress
            this.weightCollected = null;      // Explicitly set to null if in progress
        } else {
            this.status = "Completed";
            this.collectionEndTime = wasteLog.getCollectionEndTime();
            this.weightCollected = wasteLog.getWeightCollected();
        }
    }
}
