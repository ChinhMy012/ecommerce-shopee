package com.example.ecommerce_backend.repository;


import com.example.ecommerce_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    default Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        Optional<User> byUsername = findByUsername(usernameOrEmail);
        if (byUsername.isPresent()) return byUsername;
        return findByEmail(usernameOrEmail);
    }
}