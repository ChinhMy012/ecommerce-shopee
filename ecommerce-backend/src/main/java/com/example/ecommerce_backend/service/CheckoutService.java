package com.example.ecommerce_backend.service;

import com.example.ecommerce_backend.dto.request.checkout.CheckoutRequest;
import com.example.ecommerce_backend.dto.response.checkout.CheckoutResponse;
import com.example.ecommerce_backend.entity.User;

public interface CheckoutService {

    public CheckoutResponse checkout( CheckoutRequest request);
}

