package com.example.ecommerce_backend.dto.response.order;


import com.example.ecommerce_backend.entity.Order;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SellerOrderResponse {

    private Long orderId;

    private String orderCode;

    private String customerUsername;

    private Order.OrderStatus status;

    private Order.PaymentStatus paymentStatus;

    private BigDecimal total;

    private BigDecimal shippingFee;

    private LocalDateTime createdAt;

    private List<SellerOrderItemResponse> items;

}