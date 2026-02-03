package com.example.ecommerce_backend.repository;

import com.example.ecommerce_backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("""
    SELECT DISTINCT p
    FROM Product p
    WHERE p.status = :status
""")
    List<Product> findAllByStatus(@Param("status") Product.ProductStatus status);
}
