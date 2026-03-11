package com.example.ecommerce_backend.repository;

import com.example.ecommerce_backend.entity.Shop;
import com.example.ecommerce_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    boolean existsByUser(User user);

    Optional<Shop> findByUser(User user);
}
