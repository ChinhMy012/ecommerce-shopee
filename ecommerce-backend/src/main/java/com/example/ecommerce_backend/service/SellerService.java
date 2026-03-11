package com.example.ecommerce_backend.service;

import com.example.ecommerce_backend.dto.request.seller.RegisterSellerRequest;

public interface SellerService {

    void registerSeller(RegisterSellerRequest request);

    void openShop();

    default void approveShop(Long shopId) {

    }
}
