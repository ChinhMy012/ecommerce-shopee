package com.example.ecommerce_backend.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AttributeResponse {
    private String name;
    private List<String> values;
}
