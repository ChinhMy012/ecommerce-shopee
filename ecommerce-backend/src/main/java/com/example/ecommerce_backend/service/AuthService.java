package com.example.ecommerce_backend.service;


import com.example.ecommerce_backend.dto.request.LoginRequest;
import com.example.ecommerce_backend.dto.request.RegisterRequest;
import com.example.ecommerce_backend.dto.response.AuthResponse;

public interface AuthService {

    /**
     * Đăng ký tài khoản mới với role mặc định = CUSTOMER
     */
    void register(RegisterRequest request);

    /**
     * Đăng nhập, trả về JWT token
     */
    AuthResponse login(LoginRequest request);
}