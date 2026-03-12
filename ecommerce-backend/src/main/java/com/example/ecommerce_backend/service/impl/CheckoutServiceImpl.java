package com.example.ecommerce_backend.service.impl;

import com.example.ecommerce_backend.dto.request.checkout.CheckoutRequest;
import com.example.ecommerce_backend.dto.response.checkout.CheckoutResponse;
import com.example.ecommerce_backend.entity.*;
import com.example.ecommerce_backend.repository.CartItemRepository;
import com.example.ecommerce_backend.repository.CustomerAddressRepository;
import com.example.ecommerce_backend.repository.OrderRepository;
import com.example.ecommerce_backend.repository.ProductVariantRepository;
import com.example.ecommerce_backend.service.CheckoutService;
import com.example.ecommerce_backend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OrderRepository orderRepository;
    private final CustomerAddressRepository addressRepository;
    private final UserService userService;


    @Override
    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request) {
        User currentUser = userService.getCurrentUser();
        // Lấy cart
        List<CartItem> cartItems =
                cartItemRepository.findByIdInAndUserId(
                        request.getCartItemIds(),
                        currentUser.getId()
                );

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("No selected cart items found");
        }

        if (cartItems.size() != request.getCartItemIds().size()) {
            throw new IllegalArgumentException("Some cart items are invalid");
        }

        // Validate address (an toàn)
        CustomerAddress address = addressRepository
                .findByIdAndUser(request.getAddressId(), currentUser)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        // Validate stock + tính tổng tiền
        BigDecimal orderTotal = BigDecimal.ZERO;
        //lock đúng record
        for (CartItem cartItem : cartItems) {

            ProductVariant variant = productVariantRepository
                    .findByIdAndStatus(
                            cartItem.getVariant().getId(),
                            ProductVariant.VariantStatus.ACTIVE
                    )
                    .orElseThrow(() ->
                            new IllegalArgumentException("Variant not found or inactive")
                    );

            if (variant.getStock() < cartItem.getQuantity()) {
                throw new IllegalStateException(
                        "Not enough stock for variant id: " + variant.getId()
                );
            }

            BigDecimal subtotal = variant.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            orderTotal = orderTotal.add(subtotal);
        }

        // Tạo Order
        Order order = Order.builder()
                .user(currentUser)
                .address(address)
                .total(orderTotal)
                .status(Order.OrderStatus.PENDING)
                .orderCode("ORD-" + System.currentTimeMillis())
                .paymentMethod(request.getPaymentMethod())
                .shippingFee(BigDecimal.ZERO)
                .paymentStatus(Order.PaymentStatus.UNPAID)
                .items(new ArrayList<>())
                .build();

        orderRepository.save(order);

        //Tạo OrderItem + trừ kho
        for (CartItem cartItem : cartItems) {

            ProductVariant variant = cartItem.getVariant();

            BigDecimal price = variant.getPrice();
            BigDecimal subtotal = price.multiply(
                    BigDecimal.valueOf(cartItem.getQuantity())
            );

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .variantId(variant.getId())
                    .variantName(variant.getProduct().getName())
                    .priceAtPurchase(price)
                    .quantity(cartItem.getQuantity())
                    .subtotal(subtotal)
                    .build();

            order.getItems().add(orderItem);

            // trừ kho
            variant.setStock(
                    variant.getStock() - cartItem.getQuantity()
            );
            productVariantRepository.save(variant);
        }

        // Clear cart
        cartItemRepository.deleteAllById(
                cartItems.stream()
                        .map(CartItem::getId)
                        .toList()
        );

        return new CheckoutResponse(
                order.getId(),
                orderTotal,
                order.getStatus(),
                order.getPaymentStatus()
        );
    }
}
