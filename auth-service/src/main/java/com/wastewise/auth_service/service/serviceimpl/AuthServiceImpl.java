package com.wastewise.auth_service.service.serviceimpl;

import com.wastewise.auth_service.dto.LoginRequestDTO;
import com.wastewise.auth_service.dto.LoginResponseDTO;
import com.wastewise.auth_service.dto.PasswordResetDTO;
import com.wastewise.auth_service.dto.RegisterWorkerDTO;
import com.wastewise.auth_service.exception.InvalidCredentialsException;
import com.wastewise.auth_service.exception.InvalidRoleException;
import com.wastewise.auth_service.exception.ResourceNotFoundException;
import com.wastewise.auth_service.exception.WorkerAlreadyExistsException;
import com.wastewise.auth_service.model.Role;
import com.wastewise.auth_service.model.User;
import com.wastewise.auth_service.repository.RoleRepository;
import com.wastewise.auth_service.repository.UserRepository;
import com.wastewise.auth_service.security.JwtUtil;
import com.wastewise.auth_service.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil,
                           PasswordEncoder passwordEncoder, RoleRepository roleRepository){
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public LoginResponseDTO login(LoginRequestDTO dto){
        Optional<User> userOpt = userRepository.findByWorkerId(dto.getWorkerId());

        if(userOpt.isEmpty()){
            throw new InvalidCredentialsException("User not found");
        }

        User user = userOpt.get();

        if(!passwordEncoder.matches(dto.getPassword(),user.getPassword())){
            throw new InvalidCredentialsException("Invalid password entered");
        }

        String token = jwtUtil.generateToken(user.getWorkerId(), user.getRole().getRoleName());

        return new LoginResponseDTO(token, user.getRole().getRoleName());
    }

    public void registerWorker(RegisterWorkerDTO dto){
        if(userRepository.existsById(dto.getWorkerId())){
            throw new WorkerAlreadyExistsException("Worker with id "+ dto.getWorkerId() + " already exists");
        }

        Role role = roleRepository.findByRoleName(dto.getRoleName())
                .orElseThrow(() -> new InvalidRoleException("Invalid Role"));

        User user = new User();
        user.setWorkerId(dto.getWorkerId());
        user.setPassword(passwordEncoder.encode(dto.getWorkerId()));
        user.setRole(role);
        user.setCreatedDate(LocalDateTime.now());
        user.setUpdatedDate(LocalDateTime.now());

        userRepository.save(user);
    }


    public void resetPassword(PasswordResetDTO dto) {
        User user = userRepository.findById(dto.getWorkerId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdatedDate(LocalDateTime.now());
        userRepository.save(user);
    }

    public LoginResponseDTO validateToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey("256-bit-secret-key") // Use your actual secret key
                .parseClaimsJws(token)
                .getBody();

        String role = claims.get("role", String.class);
        return new LoginResponseDTO(token, role);
    }


}