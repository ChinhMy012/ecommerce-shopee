package com.example.ecommerce_backend.controller;

import com.example.ecommerce_backend.dto.request.product.UpdateProductStatusRequest;
import com.example.ecommerce_backend.dto.response.product.ProductDetailResponse;
import com.example.ecommerce_backend.dto.response.product.ProductResponse;
import com.example.ecommerce_backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/all")
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }


    @GetMapping("/{slug}")
    public ResponseEntity<ProductDetailResponse> getDetail(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getProductDetail(slug));
    }


    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ADMIN_UPDATE_STATUS_PRODUCT')")
    public ResponseEntity<?> updateStatus(
            @PathVariable Integer id,
            @RequestBody UpdateProductStatusRequest request
    ) {
        productService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok("Status updated");
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ADMIN_UPDATE_STATUS_PRODUCT')")
    public ResponseEntity<?> getPendingProducts() {

        return ResponseEntity.ok(productService.getPendingProducts());
    }
}
