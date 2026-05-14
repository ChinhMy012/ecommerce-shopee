package com.example.ecommerce_backend.controller;

import com.example.ecommerce_backend.dto.request.login.LoginRequest;
import com.example.ecommerce_backend.dto.request.login.RegisterRequest;
import com.example.ecommerce_backend.dto.response.login.AuthResponse;
import com.example.ecommerce_backend.service.AuthService;
import com.example.ecommerce_backend.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        // Chỉ gọi Service để lấy data
        AuthResponse tokens = authService.login(request);

        // Dùng Utility để xử lý việc ghi Cookie (Không viết code ResponseCookie ở đây)
        cookieUtil.setAuthCookies(response, tokens.getAccessToken(), tokens.getRefreshToken());

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        // Toàn bộ logic kiểm tra token, lấy User, tạo token mới ĐÃ NẰM TRONG SERVICE
        AuthResponse tokens = authService.refreshToken(refreshToken);

        // Ghi đè cookie mới (Xoay vòng token)
        cookieUtil.setAuthCookies(response, tokens.getAccessToken(), tokens.getRefreshToken());

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "access_token", required = false) String accessToken,
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {

        // Báo cho Service để đưa vào Redis Blacklist
        authService.logout(accessToken, refreshToken);

        // Xóa sạch cookie
        cookieUtil.deleteAuthCookies(response);

        return ResponseEntity.ok().build();
    }
}