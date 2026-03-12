package com.example.ecommerce_backend.dto.request.product;

import lombok.Data;

@Data
public class VariantAttributeRequest {
    private String attributeName;

    private String attributeValue;
}
