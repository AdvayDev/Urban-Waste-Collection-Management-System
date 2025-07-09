package com.wastewise.auth_service.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    static class TestController {
        @GetMapping("/test/unauthorized")
        void unauthorized() {
            throw new InvalidCredentialsException("No access");
        }
        @GetMapping("/test/badrequest")
        void badRequest() {
            throw new InvalidRoleException("Bad role");
        }
        @GetMapping("/test/notfound")
        void notFound() {
            throw new ResourceNotFoundException("Missing");
        }
        @GetMapping("/test/conflict")
        void conflict() {
            throw new WorkerAlreadyExistsException("Exists");
        }
        @GetMapping("/test/generic")
        void generic() {
            throw new RuntimeException("Boom");
        }
    }

    @Test
    void handleUnauthorized() throws Exception {
        mockMvc.perform(get("/test/unauthorized"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void handleBadRequest() throws Exception {
        mockMvc.perform(get("/test/badrequest"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void handleNotFound() throws Exception {
        mockMvc.perform(get("/test/notfound"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Missing"));
    }

    @Test
    void handleConflict() throws Exception {
        mockMvc.perform(get("/test/conflict"))
                .andExpect(status().isConflict());
    }

    @Test
    void handleGeneric() throws Exception {
        mockMvc.perform(get("/test/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }
}