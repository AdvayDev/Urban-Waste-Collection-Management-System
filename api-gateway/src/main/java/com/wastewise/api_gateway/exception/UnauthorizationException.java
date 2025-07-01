package com.wastewise.api_gateway.exception;

public class UnauthorizationException extends RuntimeException{
    public UnauthorizationException(String message) {
        super(message);
    }
}
