package com.example.ecommerce_backend.repository;

import com.example.ecommerce_backend.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
