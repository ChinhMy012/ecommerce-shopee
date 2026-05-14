package com.example.ecommerce_backend.service;

import com.example.ecommerce_backend.dto.request.login.LoginRequest;
import com.example.ecommerce_backend.dto.request.login.RegisterRequest;
import com.example.ecommerce_backend.dto.response.login.AuthResponse;

public interface AuthService {

    /**
     * Đăng ký tài khoản mới với role mặc định = CUSTOMER
     */
    void register(RegisterRequest request);

    /**
     * Đăng nhập, trả về accessToken + refreshToken (JWT)
     */
    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(String refreshToken); // Phương thức mới
    void logout(String accessToken, String refreshToken);
}
