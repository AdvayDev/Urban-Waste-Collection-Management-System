package com.wastewise.auth_service.repository;

import com.wastewise.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByWorkerId(String Id);
}