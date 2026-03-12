package com.example.ecommerce_backend.dto.response.product;

import com.example.ecommerce_backend.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminProductReviewResponse {

    private Long productId;

    private String productName;

    private String description;

    private String shopName;

    private String sellerUsername;

}
