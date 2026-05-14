package com.example.ecommerce_backend.dto;

import com.example.ecommerce_backend.dto.response.login.AuthResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseCookie;

@Data
@AllArgsConstructor
public class TokenWrapper {
    private AuthResponse authResponse;
    private ResponseCookie accessCookie;
    private ResponseCookie refreshCookie;
}