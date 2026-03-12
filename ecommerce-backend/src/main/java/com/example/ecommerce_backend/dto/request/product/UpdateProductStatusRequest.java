package com.example.ecommerce_backend.dto.request.product;

import com.example.ecommerce_backend.entity.Product;
import lombok.Data;

@Data
public class UpdateProductStatusRequest {
    private Product.ProductStatus status;
}
