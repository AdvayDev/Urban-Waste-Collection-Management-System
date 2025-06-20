package com.wastewise.worker.management.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleWorkerNotFound_shouldReturnNotFound() {
        WorkerNotFoundException ex = new WorkerNotFoundException("ID 10");
        ResponseEntity<String> response = exceptionHandler.handleWorkerNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Worker not found: ID 10"));
    }

    @Test
    void handleResourceNotFound_shouldReturnNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Bin 101");
        ResponseEntity<String> response = exceptionHandler.handleResourceNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Resource not found: Bin 101"));
    }

    @Test
    void handleWorkersAlreadyAssignedException_shouldReturnConflict() {
        WorkersAlreadyAssignedException ex = new WorkersAlreadyAssignedException("Worker 12 is already assigned");
        ResponseEntity<String> response = exceptionHandler.handleWorkersAlreadyAssignedException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().contains("Worker not available error: Worker 12 is already assigned"));
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() {
        Exception ex = new Exception("Something went wrong");
        ResponseEntity<String> response = exceptionHandler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error: Something went wrong"));
    }

    @Test
    void handleIllegalState_shouldReturnConflict() {
        IllegalStateException ex = new IllegalStateException("Invalid state transition");
        ResponseEntity<String> response = exceptionHandler.handleIllegalState(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().contains("Illegal State Error: Invalid state transition"));
    }

    @Test
    void handleContactInformationUsedException_shouldReturnConflict() {
        ContactInformationUsedException ex = new ContactInformationUsedException("Phone number already in use");
        ResponseEntity<String> response = exceptionHandler.handleContactInformationUsedException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().contains("Contact details already used: Phone number already in use"));
    }

    @Test
    void handleValidationExceptions_shouldReturnBadRequestWithFieldError() {
        // Mocking BindingResult and FieldError
        BindingResult mockBindingResult = mock(BindingResult.class);
        List<FieldError> fieldErrors = Collections.singletonList(
                new FieldError("worker", "email", "must be a valid email")
        );
        when(mockBindingResult.getFieldErrors()).thenReturn(fieldErrors);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, mockBindingResult);

        ResponseEntity<String> response = exceptionHandler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid input provided"));
        assertTrue(response.getBody().contains("email: must be a valid email"));
    }
}