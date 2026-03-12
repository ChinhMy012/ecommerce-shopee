package com.example.ecommerce_backend.dto.request.product;

import com.example.ecommerce_backend.dto.request.image.ProductImageRequest;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class CreateProductRequest {

    private String name;

    private String description;

    private String slug;

    private Set<Long> categoryIds;

    private List<ProductImageRequest> images;

    private List<VariantRequest> variants;
}
