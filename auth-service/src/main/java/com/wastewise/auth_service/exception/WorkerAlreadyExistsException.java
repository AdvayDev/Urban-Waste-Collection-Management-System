package com.wastewise.auth_service.exception;

public class WorkerAlreadyExistsException extends RuntimeException{
    public WorkerAlreadyExistsException(String message) {
        super(message);
    }
}