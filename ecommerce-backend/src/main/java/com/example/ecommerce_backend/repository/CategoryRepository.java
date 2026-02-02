package com.example.ecommerce_backend.repository;

import com.example.ecommerce_backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Lấy category gốc
    List<Category> findByParentIsNullAndStatus(Category.CategoryStatus status);

}
