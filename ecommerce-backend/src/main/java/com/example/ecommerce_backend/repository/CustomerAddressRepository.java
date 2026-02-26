package com.example.ecommerce_backend.repository;

import com.example.ecommerce_backend.entity.CustomerAddress;
import com.example.ecommerce_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress,Long> {

    List<CustomerAddress> findByUser(User user);


    Optional<CustomerAddress> findByIdAndUser(Long id, User user);
}
