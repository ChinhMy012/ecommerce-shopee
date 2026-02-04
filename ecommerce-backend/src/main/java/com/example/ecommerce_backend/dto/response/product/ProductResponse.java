package com.example.ecommerce_backend.dto.response.product;

import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductResponse {
    private Long id;
    private String slug;
    private String name;

    private String thumbnail;   // ⭐ ẢNH ĐẠI DIỆN

    private Double minPrice;    // ⭐ Giá thấp nhất
    private Double maxPrice;    // (optional)

    private List<String> categories;
}
