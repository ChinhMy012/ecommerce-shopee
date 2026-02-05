package com.example.ecommerce_backend.dto.response.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;



@Getter
@Setter
@NoArgsConstructor   // 🔥 BẮT BUỘC
@AllArgsConstructor
public class CartItemResponse {
    private Long cartItemId;

    // Product
    private Long productId;
    private String productName;
    private String productSlug;

    // Variant
    private Long variantId;
    private String sku;
    private BigDecimal price;

    private Map<String, String> attributes;

    // Images
    private List<String> images;

    private Integer quantity;
    private Integer stock;

    private BigDecimal totalPrice;

    // Optional nhưng rất nên có
    private boolean available;
}
