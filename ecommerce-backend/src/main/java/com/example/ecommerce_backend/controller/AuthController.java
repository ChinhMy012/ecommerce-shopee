package com.example.ecommerce_backend.controller;

import com.example.ecommerce_backend.dto.request.login.LoginRequest;
import com.example.ecommerce_backend.dto.request.login.RegisterRequest;
import com.example.ecommerce_backend.dto.response.login.AuthResponse;
import com.example.ecommerce_backend.entity.Permission;
import com.example.ecommerce_backend.entity.User;
import com.example.ecommerce_backend.repository.UserRepository;
import com.example.ecommerce_backend.service.AuthService;
import com.example.ecommerce_backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse tokens = authService.login(request);

        ResponseCookie accessCookie = ResponseCookie.from("access_token", tokens.getAccessToken()).httpOnly(true).secure(false).path("/").maxAge(15 * 60).build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", tokens.getRefreshToken()).httpOnly(true).secure(false).path("/").maxAge(7 * 24 * 60 * 60).build();

        return ResponseEntity.ok().header("Set-Cookie", accessCookie.toString()).header("Set-Cookie", refreshCookie.toString())
                // ✅ TRẢ TOKEN THẬT
                .body(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie accessCookie = ResponseCookie.from("access_token", "").httpOnly(true).path("/").maxAge(0).build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "").httpOnly(true).path("/").maxAge(0).build();

        return ResponseEntity.ok().header("Set-Cookie", accessCookie.toString()).header("Set-Cookie", refreshCookie.toString()).build();
    }

    // ========================
    // REFRESH TOKEN
    // ========================
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@CookieValue(name = "refresh_token", required = false) String refreshToken) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        String username;
        try {
            username = jwtService.extractUsername(refreshToken);
            if (!jwtService.isTokenValid(refreshToken, username)) {
                return ResponseEntity.status(401).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

        // Lấy user từ DB
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

// Lấy permissions đang ACTIVE
        Set<String> authorities = user.getRole().getPermissions().stream().filter(p -> p.getStatus() == Permission.PermissionStatus.ACTIVE).map(Permission::getName).collect(Collectors.toSet());

// Tạo access token mới
        String newAccessToken = jwtService.generateToken(username, authorities);

        // Trả về cookie access token mới
        ResponseCookie accessCookie = ResponseCookie.from("access_token", newAccessToken).httpOnly(true).secure(false).path("/").maxAge(15 * 60).build();

        return ResponseEntity.ok().header("Set-Cookie", accessCookie.toString()).body(new AuthResponse(newAccessToken, null, null));
    }
}
