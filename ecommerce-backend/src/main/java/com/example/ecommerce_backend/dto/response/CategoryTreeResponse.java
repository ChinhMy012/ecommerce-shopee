package com.example.ecommerce_backend.dto.response;

import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor   // 🔥 BẮT BUỘC
@AllArgsConstructor
@Data
public class CategoryTreeResponse {
    private Long id;
    private String name;
    private List<CategoryTreeResponse> children;

}
