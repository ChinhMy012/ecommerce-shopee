package com.example.ecommerce_backend.controller;

import com.example.ecommerce_backend.dto.response.product.ProductDetailResponse;
import com.example.ecommerce_backend.dto.response.product.ProductResponse;
import com.example.ecommerce_backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    @GetMapping("/all")
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    @GetMapping("/{slug}")
    public ResponseEntity<ProductDetailResponse> getDetail(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getProductDetail(slug));
    }
}
