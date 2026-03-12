package com.example.ecommerce_backend.dto.request.order;

import com.example.ecommerce_backend.entity.Order;

public class CreateOrderRequest {
    private Long addressId;

    private Order.PaymentMethod paymentMethod;
}
