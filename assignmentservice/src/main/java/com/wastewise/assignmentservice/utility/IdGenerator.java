package com.wastewise.assignmentservice.utility;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IdGenerator {

    private final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    public String generateAssignmentId() {
        String prefix = "A";
        counters.putIfAbsent(prefix, new AtomicInteger(0));
        int id = counters.get(prefix).incrementAndGet();
        return prefix + String.format("%03d", id);
    }
}
