package com.example.ecommerce_backend.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductDetailResponse {
    private Long id;
    private String name;
    private String description;
    private String slug;

    private List<String> images;

    private List<AttributeResponse> attributes;
    private List<VariantResponse> variants;

    private String shopName;
    private String shopDescription;
    private Long shopId;
}
