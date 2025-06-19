package com.wastewise.auth_service.service.serviceimpl;

import com.wastewise.auth_service.dto.LoginRequestDTO;
import com.wastewise.auth_service.dto.LoginResponseDTO;
import com.wastewise.auth_service.model.User;
import com.wastewise.auth_service.repository.UserRepository;
import com.wastewise.auth_service.security.JwtUtil;
import com.wastewise.auth_service.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDTO login(LoginRequestDTO dto){
        Optional<User> userOpt = userRepository.findByWorkerId(dto.getWorkerId());

        if(userOpt.isEmpty()){
            throw new
        }
    }
}
