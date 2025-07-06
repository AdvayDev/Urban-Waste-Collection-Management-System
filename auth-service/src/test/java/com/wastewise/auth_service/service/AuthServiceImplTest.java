package com.wastewise.auth_service.service;

import com.wastewise.auth_service.dto.*;
import com.wastewise.auth_service.exception.InvalidCredentialsException;
import com.wastewise.auth_service.exception.InvalidRoleException;
import com.wastewise.auth_service.exception.ResourceNotFoundException;
import com.wastewise.auth_service.exception.WorkerAlreadyExistsException;
import com.wastewise.auth_service.model.Role;
import com.wastewise.auth_service.model.User;
import com.wastewise.auth_service.repository.RoleRepository;
import com.wastewise.auth_service.repository.UserRepository;
import com.wastewise.auth_service.security.JwtUtil;
import com.wastewise.auth_service.service.serviceimpl.AuthServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private final String workerId = "W001";
    private final String password = "password123";
    private final String roleName = "ADMIN";
    private final String secret = "256-bit-secret-key-to-generate-the-encoded-message";

    @Test
    void testLoginSuccess() {
        Role role = new Role();
        role.setRoleName(roleName);
        User user = new User(workerId, passwordEncoder.encode(password), role);

        when(userRepository.findByWorkerId(workerId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(workerId, roleName)).thenReturn("mock-jwt-token");

        LoginRequestDTO request = new LoginRequestDTO(workerId, password);
        LoginResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals(roleName, response.getRole());
    }

    @Test
    void testLoginInvalidUser() {
        when(userRepository.findByWorkerId(workerId)).thenReturn(Optional.empty());

        LoginRequestDTO request = new LoginRequestDTO(workerId, password);
        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void testLoginWrongPassword() {
        Role role = new Role();
        role.setRoleName(roleName);
        User user = new User(workerId, "wrong-hash", role);

        when(userRepository.findByWorkerId(workerId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        LoginRequestDTO request = new LoginRequestDTO(workerId, password);
        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void testRegisterWorkerSuccess() {
        RegisterWorkerDTO dto = new RegisterWorkerDTO(workerId, roleName);
        Role role = new Role();
        role.setRoleName(roleName);

        when(userRepository.existsById(workerId)).thenReturn(false);
        when(roleRepository.findByRoleName(roleName)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(workerId)).thenReturn("encoded-password");

        assertDoesNotThrow(() -> authService.registerWorker(dto));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterWorkerAlreadyExists() {
        RegisterWorkerDTO dto = new RegisterWorkerDTO(workerId, roleName);
        when(userRepository.existsById(workerId)).thenReturn(true);

        assertThrows(WorkerAlreadyExistsException.class, () -> authService.registerWorker(dto));
    }

    @Test
    void testRegisterWorkerInvalidRole() {
        RegisterWorkerDTO dto = new RegisterWorkerDTO(workerId, roleName);

        when(userRepository.existsById(workerId)).thenReturn(false);
        when(roleRepository.findByRoleName(roleName)).thenReturn(Optional.empty());

        assertThrows(InvalidRoleException.class, () -> authService.registerWorker(dto));
    }

    @Test
    void testResetPasswordSuccess() {
        PasswordResetDTO dto = new PasswordResetDTO(workerId, "newPass");
        User user = new User();

        when(userRepository.findById(workerId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(dto.getNewPassword())).thenReturn("encoded-newPass");

        assertDoesNotThrow(() -> authService.resetPassword(dto));
        verify(userRepository).save(user);
        assertEquals("encoded-newPass", user.getPassword());
    }

    @Test
    void testResetPasswordUserNotFound() {
        PasswordResetDTO dto = new PasswordResetDTO(workerId, "newPass");
        when(userRepository.findById(workerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.resetPassword(dto));
    }

}