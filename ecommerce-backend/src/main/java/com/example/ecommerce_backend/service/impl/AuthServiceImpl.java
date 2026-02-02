package com.example.ecommerce_backend.service.impl;



import com.example.ecommerce_backend.dto.request.LoginRequest;
import com.example.ecommerce_backend.dto.request.RegisterRequest;
import com.example.ecommerce_backend.dto.response.AuthResponse;
import com.example.ecommerce_backend.entity.Permission;
import com.example.ecommerce_backend.entity.Role;
import com.example.ecommerce_backend.entity.User;
import com.example.ecommerce_backend.repository.RoleRepository;
import com.example.ecommerce_backend.repository.UserRepository;
import com.example.ecommerce_backend.service.AuthService;
import com.example.ecommerce_backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        // Rule: Khi đăng ký → role = CUSTOMER
        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER not found. Please seed roles table."));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(customerRole)
                .build();

        userRepository.save(user);
    }
    @Override
    public AuthResponse login(LoginRequest request) {

        // 1. Validate request
        if (request == null) {
            throw new RuntimeException("Request body is missing");
        }

        String usernameOrEmail = request.getUsernameOrEmail();
        String password = request.getPassword();

        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            throw new RuntimeException("Username or email is required");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Password is required");
        }

        // 2. Find user
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new RuntimeException("Invalid username/email or password"));

        // 3. Check password
        boolean matched = passwordEncoder.matches(password, user.getPasswordHash());
        if (!matched) {
            throw new RuntimeException("Invalid username/email or password");
        }

        // =======================
        // 🔥 4. LOAD PERMISSIONS
        // =======================
        Set<String> authorities =
                user.getRole()
                        .getPermissions()              // Set<Permission>
                        .stream()
                        .filter(p -> p.getStatus() == Permission.PermissionStatus.ACTIVE)
                        .map(Permission::getName)
                        .collect(Collectors.toSet());


        // =======================
        // 🔥 5. GENERATE TOKENS
        // =======================
        String accessToken = jwtService.generateToken(
                user.getUsername(),
                authorities
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.getUsername()
        );

        return new AuthResponse(accessToken, refreshToken,authorities);
    }
}