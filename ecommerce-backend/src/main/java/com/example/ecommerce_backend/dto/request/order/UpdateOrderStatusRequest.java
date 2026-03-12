package com.example.ecommerce_backend.dto.request.order;

import com.example.ecommerce_backend.entity.Order;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {

    private Order.OrderStatus status;

}