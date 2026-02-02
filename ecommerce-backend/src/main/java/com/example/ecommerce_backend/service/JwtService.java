package com.example.ecommerce_backend.service;

import java.util.List;
import java.util.Set;

public interface JwtService {

    /**
     * Tạo access token (sống ngắn)
     */
    String generateToken(String subject, Set<String> authorities);

    /**
     * Tạo refresh token (sống dài)
     */
    String generateRefreshToken(String subject);

    /**
     * Trích username từ token
     */
    String extractUsername(String token);


    List<String> extractAuthorities(String token);
    /**
     * Kiểm tra token hợp lệ với username
     */
    boolean isTokenValid(String token, String username);




}
