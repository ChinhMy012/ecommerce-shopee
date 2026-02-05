package com.example.ecommerce_backend.dto.request.cart;

import com.example.ecommerce_backend.entity.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequest {
    private Long variantId;
    private Integer quantity;
}
