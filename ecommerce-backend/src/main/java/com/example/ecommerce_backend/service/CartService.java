package com.example.ecommerce_backend.service;

import com.example.ecommerce_backend.dto.request.cart.AddToCartRequest;
import com.example.ecommerce_backend.dto.response.cart.CartResponse;

public interface CartService {
    CartResponse getMyCart();

    CartResponse addToCart(AddToCartRequest request);

    CartResponse updateQuantity(Long cartItemId, Integer quantity);

    CartResponse removeItem(Long cartItemId);

    void clearCart();
}
