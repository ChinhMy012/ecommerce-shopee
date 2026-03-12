package com.example.ecommerce_backend.service.impl;

import com.example.ecommerce_backend.dto.request.seller.RegisterSellerRequest;
import com.example.ecommerce_backend.dto.response.order.SellerOrderItemResponse;
import com.example.ecommerce_backend.dto.response.order.SellerOrderResponse;
import com.example.ecommerce_backend.entity.*;
import com.example.ecommerce_backend.repository.*;
import com.example.ecommerce_backend.service.SellerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SellerServiceImlp implements SellerService {
    private final ShopRepository shopRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductVariantRepository productVariantRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    @Transactional
    public void registerSeller(RegisterSellerRequest request) {

        User user = getCurrentUser();

        if (shopRepository.existsByUser(user)) {
            throw new RuntimeException("User already has shop");
        }

        Shop shop = new Shop();
        shop.setName(request.getShopName());
        shop.setDescription(request.getDescription());
        shop.setUser(user);
        shop.setApprovalStatus(Shop.ShopApprovalStatus.PENDING);
        shop.setOperationalStatus(Shop.ShopOperationalStatus.INACTIVE);
        shop.setCreatedAt(LocalDateTime.now());

        shopRepository.save(shop);

        Role sellerRole = roleRepository
                .findByName("SELLER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRole(sellerRole);
    }

    @Override
    @Transactional
    public void approveShop(Long shopId) {

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        shop.setApprovalStatus(Shop.ShopApprovalStatus.APPROVED);
    }

    @Override
    @Transactional
    public void openShop() {

        User user = getCurrentUser();

        Shop shop = shopRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        if (shop.getApprovalStatus() != Shop.ShopApprovalStatus.APPROVED) {
            throw new RuntimeException("Shop not approved yet");
        }

        shop.setOperationalStatus(Shop.ShopOperationalStatus.ACTIVE);
    }
    @Override
    public List<SellerOrderResponse> getSellerOrders() {

        User seller = getCurrentUser();

        List<Order> allOrders = orderRepository.findAllByOrderByCreatedAtDesc();

        List<SellerOrderResponse> result = new ArrayList<>();

        for (Order order : allOrders) {

            List<SellerOrderItemResponse> sellerItems = new ArrayList<>();

            for (OrderItem item : order.getItems()) {

                ProductVariant variant = productVariantRepository
                        .findById(item.getVariantId())
                        .orElse(null);

                if (variant == null) continue;

                Shop shop = variant.getProduct().getShop();

                if (!shop.getUser().getId().equals(seller.getId())) {
                    continue;
                }

                sellerItems.add(
                        SellerOrderItemResponse.builder()
                                .orderItemId(item.getId())
                                .variantId(item.getVariantId())
                                .variantName(item.getVariantName())
                                .price(item.getPriceAtPurchase())
                                .quantity(item.getQuantity())
                                .subtotal(item.getSubtotal())
                                .build()
                );
            }

            if (!sellerItems.isEmpty()) {

                result.add(
                        SellerOrderResponse.builder()
                                .orderId(order.getId())
                                .orderCode(order.getOrderCode())
                                .customerUsername(order.getUser().getUsername())
                                .status(order.getStatus())
                                .paymentStatus(order.getPaymentStatus())
                                .total(order.getTotal())
                                .shippingFee(order.getShippingFee())
                                .createdAt(order.getCreatedAt())
                                .items(sellerItems)
                                .build()
                );
            }
        }

        return result;
    }

    @Override
    @Transactional
    public SellerOrderResponse updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);

        orderRepository.save(order);

        return SellerOrderResponse.builder()
                .orderId(order.getId())
                .orderCode(order.getOrderCode())
                .customerUsername(order.getUser().getUsername())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .total(order.getTotal())
                .shippingFee(order.getShippingFee())
                .createdAt(order.getCreatedAt())
                .items(List.of())
                .build();
    }

    private void validateStatusTransition(Order.OrderStatus current, Order.OrderStatus next) {

        switch (current) {

            case PENDING -> {
                if (next != Order.OrderStatus.CONFIRMED)
                    throw new RuntimeException("Invalid status transition");
            }

            case CONFIRMED -> {
                if (next != Order.OrderStatus.SHIPPING)
                    throw new RuntimeException("Invalid status transition");
            }

            case SHIPPING -> {
                if (next != Order.OrderStatus.COMPLETED)
                    throw new RuntimeException("Invalid status transition");
            }

            default -> throw new RuntimeException("Order cannot change status");
        }
    }
}
