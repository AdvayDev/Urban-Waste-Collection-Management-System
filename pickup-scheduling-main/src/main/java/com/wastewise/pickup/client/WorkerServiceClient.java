package com.wastewise.pickup.client;

import com.wastewise.pickup.configuration.FeignClientPickup;
import com.wastewise.pickup.dto.WorkerDto;
import com.wastewise.pickup.dto.WorkerStatusUpdateDto;
import com.wastewise.pickup.model.enums.WorkerStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "worker.management", configuration = FeignClientPickup.class)
public interface WorkerServiceClient {

    @GetMapping("/wastewise/admin/workers/internal/exists")
    Boolean checkWorkerExists(@RequestParam("workerId") String workerId);

    @PutMapping("/wastewise/admin/workers/internal/status")
    String updateWorkerStatus(@RequestBody WorkerStatusUpdateDto dto);
}
