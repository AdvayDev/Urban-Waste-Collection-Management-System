package com.wastewise.workermanagement.exception;

public class WorkersAlreadyAssignedException extends RuntimeException{
    public WorkersAlreadyAssignedException(String message) {
        super(message);
    }
}
