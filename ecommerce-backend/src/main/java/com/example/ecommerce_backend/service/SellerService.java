package com.example.ecommerce_backend.service;

import com.example.ecommerce_backend.dto.request.seller.RegisterSellerRequest;
import com.example.ecommerce_backend.dto.response.order.SellerOrderResponse;
import com.example.ecommerce_backend.entity.Order;

import java.util.List;

public interface SellerService {

    void registerSeller(RegisterSellerRequest request);

    void openShop();

    default void approveShop(Long shopId) {

    }
    List<SellerOrderResponse> getSellerOrders();

    SellerOrderResponse updateOrderStatus(Long orderId, Order.OrderStatus status);
}
