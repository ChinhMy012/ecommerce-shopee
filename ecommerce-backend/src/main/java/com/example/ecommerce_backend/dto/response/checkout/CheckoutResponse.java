package com.example.ecommerce_backend.dto.response.checkout;

import com.example.ecommerce_backend.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CheckoutResponse {
    private Long orderId;
    private BigDecimal total;
    private Order.OrderStatus status;
    private Order.PaymentStatus paymentStatus;
}
