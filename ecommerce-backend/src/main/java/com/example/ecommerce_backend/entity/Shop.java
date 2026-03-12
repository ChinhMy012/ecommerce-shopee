package com.example.ecommerce_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shops")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Chủ shop

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShopApprovalStatus approvalStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShopOperationalStatus operationalStatus;
    private LocalDateTime createdAt;

    public enum ShopApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
    public enum ShopOperationalStatus {
        INACTIVE,
        ACTIVE,
        SUSPENDED
    }
}