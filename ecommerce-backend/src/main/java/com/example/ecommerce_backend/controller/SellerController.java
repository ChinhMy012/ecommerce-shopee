package com.example.ecommerce_backend.controller;


import com.example.ecommerce_backend.dto.request.product.CreateProductRequest;
import com.example.ecommerce_backend.dto.request.seller.RegisterSellerRequest;
import com.example.ecommerce_backend.service.ProductService;
import com.example.ecommerce_backend.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;
    private final ProductService productService;


    @PostMapping("/register")
    public String registerSeller(@RequestBody RegisterSellerRequest request) {
        sellerService.registerSeller(request);
        return "Seller registration submitted";
    }

    @PutMapping("/open")
    public String openShop() {
        sellerService.openShop();
        return "Shop is now ACTIVE";
    }

    @PutMapping("/admin/shops/{id}/approve")
    public String approveShop(@PathVariable Long id) {
        sellerService.approveShop(id);
        return "Shop approved";
    }

    @PostMapping("/product")
    @PreAuthorize("hasAuthority('SELLER_ADD_PRODUCT')")
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody CreateProductRequest request
    ) {

        productService.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Product created successfully");
    }
}