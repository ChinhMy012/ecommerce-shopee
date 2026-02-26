package com.example.ecommerce_backend.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Thuộc về order nào
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // ===== SNAPSHOT DATA (KHÔNG join Product) =====

    // ID variant tại thời điểm mua (để trace)
    @Column(nullable = false)
    private Long variantId;

    // Tên variant tại thời điểm mua (hiển thị lịch sử)
    @Column(nullable = false)
    private String variantName;

    // Giá tại thời điểm mua (GIÁ CHỐT)
    @Column(nullable = false)
    private BigDecimal priceAtPurchase;

    @Column(nullable = false)
    private Integer quantity;

    // Tiền của riêng item này
    @Column(nullable = false)
    private BigDecimal subtotal;
}