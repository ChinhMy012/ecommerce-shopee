package com.example.ecommerce_backend.controller;
import com.example.ecommerce_backend.dto.request.checkout.CheckoutRequest;
import com.example.ecommerce_backend.dto.response.checkout.CheckoutResponse;
import com.example.ecommerce_backend.entity.User;
import com.example.ecommerce_backend.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<CheckoutResponse> checkout(
            @RequestBody CheckoutRequest request
    ) {
        CheckoutResponse response = checkoutService.checkout(request);
        return ResponseEntity.ok(response);
    }
}