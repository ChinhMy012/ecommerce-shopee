package com.example.ecommerce_backend.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "product_variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String sku;
    private BigDecimal price;
    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL)
    private List<ProductVariantAttribute> attributes;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL)
    private List<Image> images;

    @Enumerated(EnumType.STRING)
    private VariantStatus status;

    public enum VariantStatus {
        ACTIVE, INACTIVE
    }
}