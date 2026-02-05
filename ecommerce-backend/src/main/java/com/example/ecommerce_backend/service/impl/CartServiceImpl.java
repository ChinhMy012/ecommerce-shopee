package com.example.ecommerce_backend.service.impl;

import com.example.ecommerce_backend.dto.request.cart.AddToCartRequest;
import com.example.ecommerce_backend.dto.response.cart.CartItemResponse;
import com.example.ecommerce_backend.dto.response.cart.CartResponse;
import com.example.ecommerce_backend.entity.*;
import com.example.ecommerce_backend.repository.CartItemRepository;
import com.example.ecommerce_backend.repository.ProductVariantRepository;
import com.example.ecommerce_backend.service.CartService;
import com.example.ecommerce_backend.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository variantRepository;
    private final UserService userService;

    public CartServiceImpl(
            CartItemRepository cartItemRepository,
            ProductVariantRepository variantRepository,
            UserService userService
    ) {
        this.cartItemRepository = cartItemRepository;
        this.variantRepository = variantRepository;
        this.userService = userService;
    }

    // =========================
    // ADD TO CART
    // =========================
    @Override
    public CartResponse addToCart(AddToCartRequest request) {

        User user = userService.getCurrentUser();

        ProductVariant variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        if (variant.getStatus() != ProductVariant.VariantStatus.ACTIVE) {
            throw new RuntimeException("Variant inactive");
        }

        int quantity = request.getQuantity();
        if (quantity <= 0) {
            throw new RuntimeException("Invalid quantity");
        }

        CartItem cartItem = cartItemRepository
                .findByUserIdAndVariantId(user.getId(), variant.getId())
                .orElse(null);

        if (cartItem == null) {
            if (quantity > variant.getStock()) {
                throw new RuntimeException("Not enough stock");
            }

            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setVariant(variant);
            cartItem.setQuantity(quantity);
            cartItem.setCreatedAt(LocalDateTime.now());
        } else {
            int newQty = cartItem.getQuantity() + quantity;
            if (newQty > variant.getStock()) {
                throw new RuntimeException("Not enough stock");
            }
            cartItem.setQuantity(newQty);
            cartItem.setUpdatedAt(LocalDateTime.now());
        }

        cartItemRepository.save(cartItem);

        return getMyCart();
    }

    // =========================
    // GET MY CART
    // =========================
    @Override
    @Transactional(readOnly = true)
    public CartResponse getMyCart() {

        User user = userService.getCurrentUser();
        List<CartItem> items = cartItemRepository.findByUserId(user.getId());

        List<CartItemResponse> responses = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (CartItem item : items) {

            CartItemResponse r = mapToCartItemResponse(item);

            totalPrice = totalPrice.add(r.getTotalPrice());
            totalQuantity += item.getQuantity();

            responses.add(r);
        }

        CartResponse res = new CartResponse();
        res.setItems(responses);
        res.setTotalPrice(totalPrice);
        res.setTotalQuantity(totalQuantity);

        return res;
    }

    // =========================
    // UPDATE QUANTITY
    // =========================
    @Override
    public CartResponse updateQuantity(Long cartItemId, Integer quantity) {

        if (quantity <= 0) {
            return removeItem(cartItemId);
        }

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        ProductVariant v = item.getVariant();

        if (quantity > v.getStock()) {
            throw new RuntimeException("Not enough stock");
        }

        item.setQuantity(quantity);
        item.setUpdatedAt(LocalDateTime.now());

        return getMyCart();
    }

    // =========================
    // REMOVE ITEM
    // =========================
    @Override
    public CartResponse removeItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
        return getMyCart();
    }

    // =========================
    // CLEAR CART
    // =========================
    @Override
    public void clearCart() {
        User user = userService.getCurrentUser();
        cartItemRepository.deleteByUserId(user.getId());
    }

    // =========================
    // MAPPER
    // =========================
    private CartItemResponse mapToCartItemResponse(CartItem item) {

        ProductVariant v = item.getVariant();
        Product p = v.getProduct();

        CartItemResponse r = new CartItemResponse();
        r.setCartItemId(item.getId());

        r.setProductId(p.getId());
        r.setProductName(p.getName());
        r.setProductSlug(p.getSlug());

        r.setVariantId(v.getId());
        r.setSku(v.getSku());
        r.setPrice(v.getPrice());

        Map<String, String> attrs = new LinkedHashMap<>();
        for (ProductVariantAttribute a : v.getAttributes()) {
            attrs.put(a.getAttributeName(), a.getAttributeValue());
        }
        r.setAttributes(attrs);

        r.setImages(
                v.getImages()
                        .stream()
                        .map(Image::getUrl)
                        .toList()
        );

        r.setQuantity(item.getQuantity());
        r.setStock(v.getStock());

        BigDecimal total = v.getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));
        r.setTotalPrice(total);

        r.setAvailable(
                v.getStatus() == ProductVariant.VariantStatus.ACTIVE &&
                        v.getStock() >= item.getQuantity()
        );

        return r;
    }
}