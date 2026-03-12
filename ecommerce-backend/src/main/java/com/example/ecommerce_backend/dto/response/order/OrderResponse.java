package com.example.ecommerce_backend.dto.response.order;

import com.example.ecommerce_backend.entity.Order;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {

    private Long id;

    private String orderCode;

    private BigDecimal shippingFee;

    private BigDecimal total;

    private Order.OrderStatus status;

    private Order.PaymentMethod paymentMethod;

    private Order.PaymentStatus paymentStatus;

    private LocalDateTime createdAt;

    private List<OrderItemResponse> items;

}