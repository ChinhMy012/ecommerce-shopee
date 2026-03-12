package com.example.ecommerce_backend.dto.request.image;

import lombok.Data;

@Data
public class ProductImageRequest {
    private String url;
    private String altText;
}
