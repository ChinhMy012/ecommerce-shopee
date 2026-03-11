package com.example.ecommerce_backend.controller;


import com.example.ecommerce_backend.dto.request.seller.RegisterSellerRequest;
import com.example.ecommerce_backend.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @PostMapping("/seller/register")
    public String registerSeller(@RequestBody RegisterSellerRequest request) {
        sellerService.registerSeller(request);
        return "Seller registration submitted";
    }

    @PutMapping("/seller/open")
    public String openShop() {
        sellerService.openShop();
        return "Shop is now ACTIVE";
    }

    @PutMapping("/admin/shops/{id}/approve")
    public String approveShop(@PathVariable Long id) {
        sellerService.approveShop(id);
        return "Shop approved";
    }
}