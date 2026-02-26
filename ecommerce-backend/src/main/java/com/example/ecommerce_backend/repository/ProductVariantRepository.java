package com.example.ecommerce_backend.repository;

import com.example.ecommerce_backend.entity.ProductVariant;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    // dùng khi cần đảm bảo variant ACTIVE
    Optional<ProductVariant> findByIdAndStatus(
            Long id,
            ProductVariant.VariantStatus status
    );
}