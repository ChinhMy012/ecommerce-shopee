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
    private ShopStatus status; // PENDING, APPROVED, REJECTED

    private LocalDateTime createdAt;

    public enum ShopStatus {
        PENDING, APPROVED, REJECTED
    }
}