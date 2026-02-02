package com.example.ecommerce_backend.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Category> children = new ArrayList<>();

    // ===== STATUS =====
    @Enumerated(EnumType.STRING) // 🔥 BẮT BUỘC
    @Column(nullable = false)
    private CategoryStatus status;

    // ===== ENUM BÊN TRONG ENTITY =====
    public enum CategoryStatus {
        ACTIVE,    // Đang sử dụng
        INACTIVE,  // Tạm ẩn
        DELETED    // Xóa mềm
    }
}