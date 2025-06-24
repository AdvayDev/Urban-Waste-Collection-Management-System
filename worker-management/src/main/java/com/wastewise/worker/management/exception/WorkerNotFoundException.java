package com.wastewise.worker.management.exception;

public class WorkerNotFoundException extends RuntimeException{
    public WorkerNotFoundException(String message) {
        super(message);
    }
}
