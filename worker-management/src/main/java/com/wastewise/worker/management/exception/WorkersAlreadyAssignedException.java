package com.wastewise.worker.management.exception;

public class WorkersAlreadyAssignedException extends RuntimeException{
    public WorkersAlreadyAssignedException(String message) {
        super(message);
    }
}
