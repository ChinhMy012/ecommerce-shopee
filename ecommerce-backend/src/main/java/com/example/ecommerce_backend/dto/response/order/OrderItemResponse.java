package com.example.ecommerce_backend.dto.response.order;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {

    private Long id;

    private Long variantId;

    private String variantName;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal subtotal;

}