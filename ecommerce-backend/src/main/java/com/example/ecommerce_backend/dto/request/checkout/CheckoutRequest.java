package com.example.ecommerce_backend.dto.request.checkout;

import com.example.ecommerce_backend.entity.Order;
import lombok.Data;

import java.util.List;

@Data
public class CheckoutRequest {
    private Long addressId;
    private Order.PaymentMethod paymentMethod;
    private List<Long> cartItemIds;
}
