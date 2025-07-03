package com.wastewise.pickup.client;

import com.wastewise.pickup.dto.WorkerDto;
import com.wastewise.pickup.dto.WorkerStatusUpdateDto;
import com.wastewise.pickup.model.enums.WorkerStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "worker-service", url = "/wastewise/admin/workers/")
public interface WorkerServiceClient {
//    @GetMapping("/wastewise/admin/workers/ids")
//    List<WorkerDto> getAllWorkers(); //WorkerInfoDTO on worker-management module

    @GetMapping("/workers/{id}")
    Boolean checkWorkerExists(@PathVariable("id") String id);

    @PatchMapping("/status/{workerId}")
    String updateWorkerStatus(@PathVariable("workerId") String workerId, @RequestBody WorkerStatus status);
}