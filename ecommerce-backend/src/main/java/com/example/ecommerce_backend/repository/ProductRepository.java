package com.example.ecommerce_backend.repository;

import com.example.ecommerce_backend.dto.response.product.AdminProductReviewResponse;
import com.example.ecommerce_backend.entity.Product;
import com.example.ecommerce_backend.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.print.DocFlavor;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("""
    SELECT DISTINCT p
    FROM Product p
    WHERE p.status = :status
""")
    List<Product> findAllByStatus(@Param("status") Product.ProductStatus status);

    List<Product> findByShop(Shop shop);

    boolean existsBySlug(String slug);

    List<Product> findByStatus(Product.ProductStatus status);

    @Query(value = """
SELECT 
    p.id,
    p.name,
    p.description,
    s.name,
    u.username
FROM products p
JOIN shops s ON p.shop_id = s.id
JOIN users u ON s.user_id = u.id
WHERE p.status = :status""", nativeQuery = true)
    List<AdminProductReviewResponse> findProductsByStatus(String status);

    Optional<Product> findBySlugAndStatus(String slug, Product.ProductStatus status);
}
