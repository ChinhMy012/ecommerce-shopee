package com.example.ecommerce_backend.dto.request.seller;

import lombok.Data;

@Data
public class RegisterSellerRequest {
    private String shopName;
    private String description;
}
