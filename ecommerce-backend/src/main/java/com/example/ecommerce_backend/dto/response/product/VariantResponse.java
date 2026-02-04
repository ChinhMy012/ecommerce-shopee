package com.example.ecommerce_backend.dto.response.product;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VariantResponse {
    private Long id;
    private String sku;
    private Double price;
    private Integer stock;

    private Map<String, String> attributes;
    // Size=M, Color=Red

    private List<String> images;
}
