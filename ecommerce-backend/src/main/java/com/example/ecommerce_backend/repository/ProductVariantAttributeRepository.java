package com.example.ecommerce_backend.repository;

import com.example.ecommerce_backend.entity.ProductVariantAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantAttributeRepository extends JpaRepository<ProductVariantAttribute, Integer> {
}
