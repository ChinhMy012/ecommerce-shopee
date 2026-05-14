package com.example.ecommerce_backend.service.impl;



import com.example.ecommerce_backend.dto.request.login.LoginRequest;
import com.example.ecommerce_backend.dto.request.login.RegisterRequest;
import com.example.ecommerce_backend.dto.response.login.AuthResponse;
import com.example.ecommerce_backend.entity.Permission;
import com.example.ecommerce_backend.entity.Role;
import com.example.ecommerce_backend.entity.User;
import com.example.ecommerce_backend.repository.RoleRepository;
import com.example.ecommerce_backend.repository.UserRepository;
import com.example.ecommerce_backend.service.AuthService;
import com.example.ecommerce_backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final RedisTemplate<String, String> redisTemplate;

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
                .status(User.UserStatus.ACTIVE)
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

        User users = userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new RuntimeException("Invalid username/email or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid username/email or password");
        }

        Set<String> authorities = getAuthorities(users); // Dùng hàm helper

        String accessToken = jwtService.generateToken(user.getUsername(), authorities);
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        return new AuthResponse(accessToken, refreshToken,authorities);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        // 1. Kiểm tra tính hợp lệ của token cũ
        String username = jwtService.extractUsername(refreshToken);
        if (username == null || !jwtService.isTokenValid(refreshToken, username)) {
            throw new RuntimeException("Refresh token không hợp lệ hoặc đã hết hạn");
        }

        // 2. Tìm User từ Database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // 3. Lấy danh sách quyền (Reuse private method)
        Set<String> authorities = getAuthorities(user);

        // 4. XOAY VÒNG TOKEN: Tạo mới cả hai
        String newAccessToken = jwtService.generateToken(username, authorities);
        String newRefreshToken = jwtService.generateRefreshToken(username);

        // Trả về bộ token mới hoàn toàn
        return new AuthResponse(newAccessToken, newRefreshToken, authorities);
    }

    // Hàm helper để dùng chung cho cả login và refresh
    private Set<String> getAuthorities(User user) {
        return user.getRole().getPermissions().stream()
                .filter(p -> p.getStatus() == Permission.PermissionStatus.ACTIVE)
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }
    @Override
    public void logout(String accessToken, String refreshToken) {
        // Vô hiệu hóa Access Token
        if (accessToken != null) {
            addToBlacklist(accessToken);
        }
        // Vô hiệu hóa Refresh Token (Xoay vòng token)
        if (refreshToken != null) {
            addToBlacklist(refreshToken);
        }
    }

    private void addToBlacklist(String token) {
        try {
            // Lấy thời gian hết hạn còn lại của token để làm TTL (Time To Live) cho Redis
            Date expiration = jwtService.extractExpiration(token);
            long ttl = expiration.getTime() - System.currentTimeMillis();

            if (ttl > 0) {
                // Key là Token, Value có thể để bất kỳ ("revoked"). Tự xóa sau khi hết TTL.
                redisTemplate.opsForValue().set(token, "revoked", ttl, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            // Token đã hết hạn thì không cần làm gì
        }
    }
}