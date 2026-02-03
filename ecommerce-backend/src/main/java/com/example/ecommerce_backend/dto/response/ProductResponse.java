package com.example.ecommerce_backend.dto.response;

import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private List<String> categories;
    private List<VariantResponse> variants;
}
