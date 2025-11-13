package com.example.ecommerce_backend.service;


public interface JwtService {

    String generateToken(String subject);

    String extractUsername(String token);

    boolean isTokenValid(String token, String username);
}