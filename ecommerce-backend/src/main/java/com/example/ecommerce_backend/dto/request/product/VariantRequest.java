package com.example.ecommerce_backend.dto.request.product;

import com.example.ecommerce_backend.dto.request.image.VariantImageRequest;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class VariantRequest {

    private String sku;

    private BigDecimal price;

    private Integer stock;

    private List<VariantAttributeRequest> attributes;

    private List<VariantImageRequest> images;

}
