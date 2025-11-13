package com.example.ecommerce_backend.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_variant_attributes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String attributeName;
    private String attributeValue;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;
}