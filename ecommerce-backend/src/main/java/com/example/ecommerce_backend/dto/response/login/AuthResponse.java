package com.example.ecommerce_backend.dto.response.login;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor   // 🔥 BẮT BUỘC
@AllArgsConstructor
@Data
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Set<String> permissions;

}