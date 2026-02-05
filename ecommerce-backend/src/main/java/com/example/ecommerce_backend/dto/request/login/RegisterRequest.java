package com.example.ecommerce_backend.dto.request.login;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    // sau này bạn muốn thêm fullName, phone thì thêm ở đây
}