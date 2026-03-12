package com.example.ecommerce_backend.dto.request.cart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCartItemRequest {
    private Long cartItemId;
    private Integer quantity;
}
