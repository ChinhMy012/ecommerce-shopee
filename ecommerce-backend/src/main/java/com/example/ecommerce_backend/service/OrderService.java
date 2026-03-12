package com.example.ecommerce_backend.service;

import com.example.ecommerce_backend.dto.response.order.OrderResponse;
import com.example.ecommerce_backend.dto.response.order.SellerOrderResponse;
import com.example.ecommerce_backend.entity.Order;

import java.util.List;

public interface OrderService {
    List<OrderResponse> getMyOrders();

    OrderResponse getOrderDetail(Long orderId);

    void cancelOrder(Long orderId);


}
