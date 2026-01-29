package com.example.ecommerce_backend.service;

public interface JwtService {

    /**
     * Tạo access token (sống ngắn)
     */
    String generateToken(String subject);

    /**
     * Tạo refresh token (sống dài)
     */
    String generateRefreshToken(String subject);

    /**
     * Trích username từ token
     */
    String extractUsername(String token);

    /**
     * Kiểm tra token hợp lệ với username
     */
    boolean isTokenValid(String token, String username);
}
