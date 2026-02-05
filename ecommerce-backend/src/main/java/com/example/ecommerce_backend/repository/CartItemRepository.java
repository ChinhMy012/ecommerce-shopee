package com.example.ecommerce_backend.repository;

import com.example.ecommerce_backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    Optional<CartItem> findByUserIdAndVariantId(Long userId, Long variantId);

    List<CartItem> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
