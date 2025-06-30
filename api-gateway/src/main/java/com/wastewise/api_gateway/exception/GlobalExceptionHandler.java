package com.wastewise.api_gateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizationException.class)
    public ResponseEntity<String> handleUnauthorizationException(UnauthorizationException ex){
        log.error("Unauthorization exception thrown");
        String errorMessage = String.format("Access Denied: %s (Status: %d)", ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(errorMessage,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred");
        String errorMessage = String.format("Error: %s (Status: %d)",ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalStateException .class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        log.error("Illegal state exception thrown");
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Illegal State Error: " + ex.getMessage());
    }
}
