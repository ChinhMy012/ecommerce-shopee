package com.example.ecommerce_backend.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VariantResponse {
    private String sku;
    private Double price;
    private Integer stock;
}
