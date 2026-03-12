package com.example.ecommerce_backend.controller;

import com.example.ecommerce_backend.dto.response.order.OrderResponse;
import com.example.ecommerce_backend.entity.Order;
import com.example.ecommerce_backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 1. Xem danh sách order
    @GetMapping("/my-orders")
    public List<OrderResponse> getMyOrders() {
        return orderService.getMyOrders();
    }

    // bị giống nhau với cái trên
    // 2. Xem chi tiết order
    @GetMapping("/{id}")
    public OrderResponse getOrderDetail(@PathVariable Long id) {
        return orderService.getOrderDetail(id);
    }

    // 3. Huỷ order
    @PatchMapping("/{id}/cancel")
    public void cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
    }

}