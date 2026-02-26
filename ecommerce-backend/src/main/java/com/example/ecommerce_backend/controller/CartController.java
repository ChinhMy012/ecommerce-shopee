package com.example.ecommerce_backend.controller;


import com.example.ecommerce_backend.dto.request.cart.AddToCartRequest;
import com.example.ecommerce_backend.dto.request.cart.UpdateCartItemRequest;
import com.example.ecommerce_backend.dto.response.cart.CartItemResponse;
import com.example.ecommerce_backend.dto.response.cart.CartResponse;
import com.example.ecommerce_backend.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isGuest = auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser");

        if (isGuest) {
            //  Guest KHÔNG ghi DB
            return ResponseEntity.ok(Map.of("mode", "GUEST", "message", "Store cart in localStorage"));
        }

        //  Logged-in → ghi DB
        CartResponse res = cartService.addToCart(request);
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/items/{id}")
    public ResponseEntity<?> updateQuantity(
            @PathVariable Long id,
            @RequestBody UpdateCartItemRequest quantity
    ) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isGuest = auth == null
                || !auth.isAuthenticated()
                || auth.getPrincipal().equals("anonymousUser");

        if (isGuest) {
            return ResponseEntity.ok(Map.of(
                    "mode", "GUEST",
                    "action", "UPDATE",
                    "message", "Update cart in localStorage"
            ));
        }

        CartResponse res = cartService.updateQuantity(id, quantity);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> removeItem(@PathVariable Long id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isGuest = auth == null
                || !auth.isAuthenticated()
                || auth.getPrincipal().equals("anonymousUser");

        if (isGuest) {
            return ResponseEntity.ok(Map.of(
                    "mode", "GUEST",
                    "action", "REMOVE",
                    "message", "Remove item from localStorage"
            ));
        }

        cartService.removeItem(id);
        return ResponseEntity.ok(Map.of(
                "mode", "USER",
                "message", "Item removed"
        ));
    }

    @DeleteMapping
    public ResponseEntity<?> clearCart() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isGuest = auth == null
                || !auth.isAuthenticated()
                || auth.getPrincipal().equals("anonymousUser");

        if (isGuest) {
            return ResponseEntity.ok(Map.of(
                    "mode", "GUEST",
                    "action", "CLEAR",
                    "message", "Clear cart in localStorage"
            ));
        }

        cartService.clearCart();
        return ResponseEntity.ok(Map.of(
                "mode", "USER",
                "message", "Cart cleared"
        ));
    }
    // =====================
    // GET CART
    // =====================
    @GetMapping
    public ResponseEntity<?> getMyCart() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isGuest =
                auth == null ||
                        auth instanceof AnonymousAuthenticationToken ||
                        "anonymousUser".equals(auth.getPrincipal());

        if (isGuest) {
            return ResponseEntity.ok(
                    Map.of(
                            "mode", "GUEST",
                            "items", List.of()
                    )
            );
        }

        return ResponseEntity.ok(cartService.getMyCart());
    }
}
