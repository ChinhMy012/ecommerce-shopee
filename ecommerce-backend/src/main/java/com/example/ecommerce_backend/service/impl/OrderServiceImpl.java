package com.example.ecommerce_backend.service.impl;

import com.example.ecommerce_backend.dto.response.order.OrderItemResponse;
import com.example.ecommerce_backend.dto.response.order.OrderResponse;
import com.example.ecommerce_backend.entity.Order;
import com.example.ecommerce_backend.entity.OrderItem;
import com.example.ecommerce_backend.entity.ProductVariant;
import com.example.ecommerce_backend.entity.User;
import com.example.ecommerce_backend.repository.OrderRepository;
import com.example.ecommerce_backend.repository.ProductVariantRepository;
import com.example.ecommerce_backend.repository.UserRepository;
import com.example.ecommerce_backend.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<OrderResponse> getMyOrders() {

        User user = getCurrentUser();

        return orderRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrderDetail(Long orderId) {

        User user = getCurrentUser();

        Order order = orderRepository
                .findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return mapToResponse(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {

        User user = getCurrentUser();

        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Cannot cancel this order");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);

        // Hoàn lại stock
        for (OrderItem item : order.getItems()) {

            ProductVariant variant = productVariantRepository
                    .findById(item.getVariantId())
                    .orElseThrow();

            variant.setStock(
                    variant.getStock() + item.getQuantity()
            );

            productVariantRepository.save(variant);
        }

        orderRepository.save(order);
    }

    private OrderResponse mapToResponse(Order order) {

        List<OrderItemResponse> itemResponses =
                order.getItems().stream()
                        .map(item -> OrderItemResponse.builder()
                                .id(item.getId())
                                .variantId(item.getVariantId())
                                .variantName(item.getVariantName())
                                .price(item.getPriceAtPurchase())
                                .quantity(item.getQuantity())
                                .subtotal(item.getSubtotal())
                                .build())
                        .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .shippingFee(order.getShippingFee())
                .total(order.getTotal())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }
}
