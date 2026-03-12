package com.example.ecommerce_backend.repository;

import com.example.ecommerce_backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

    @Query("""
        SELECT oi
        FROM OrderItem oi
        JOIN ProductVariant v ON oi.variantId = v.id
        JOIN Product p ON v.product.id = p.id
        WHERE p.shop.user.id = :sellerId
    """)
    List<OrderItem> findSellerOrderItems(Long sellerId);
}
