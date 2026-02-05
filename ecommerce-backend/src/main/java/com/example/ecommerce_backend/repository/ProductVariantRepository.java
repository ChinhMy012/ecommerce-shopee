package com.example.ecommerce_backend.repository;

import com.example.ecommerce_backend.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    // dùng khi cần đảm bảo variant ACTIVE
    Optional<ProductVariant> findByIdAndStatus(
            Long id,
            ProductVariant.VariantStatus status
    );
}