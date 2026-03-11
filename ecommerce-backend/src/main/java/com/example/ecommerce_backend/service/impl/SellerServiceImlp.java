package com.example.ecommerce_backend.service.impl;

import com.example.ecommerce_backend.dto.request.seller.RegisterSellerRequest;
import com.example.ecommerce_backend.entity.Role;
import com.example.ecommerce_backend.entity.Shop;
import com.example.ecommerce_backend.entity.User;
import com.example.ecommerce_backend.repository.RoleRepository;
import com.example.ecommerce_backend.repository.ShopRepository;
import com.example.ecommerce_backend.repository.UserRepository;
import com.example.ecommerce_backend.service.SellerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class SellerServiceImlp implements SellerService {
    private final ShopRepository shopRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    @Transactional
    public void registerSeller(RegisterSellerRequest request) {

        User user = getCurrentUser();

        if (shopRepository.existsByUser(user)) {
            throw new RuntimeException("User already has shop");
        }

        Shop shop = new Shop();
        shop.setName(request.getShopName());
        shop.setDescription(request.getDescription());
        shop.setUser(user);
        shop.setApprovalStatus(Shop.ShopApprovalStatus.PENDING);
        shop.setOperationalStatus(Shop.ShopOperationalStatus.INACTIVE);
        shop.setCreatedAt(LocalDateTime.now());

        shopRepository.save(shop);

        Role sellerRole = roleRepository
                .findByName("SELLER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRole(sellerRole);
    }

    @Override
    @Transactional
    public void approveShop(Long shopId) {

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        shop.setApprovalStatus(Shop.ShopApprovalStatus.APPROVED);
    }

    @Override
    @Transactional
    public void openShop() {

        User user = getCurrentUser();

        Shop shop = shopRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        if (shop.getApprovalStatus() != Shop.ShopApprovalStatus.APPROVED) {
            throw new RuntimeException("Shop not approved yet");
        }

        shop.setOperationalStatus(Shop.ShopOperationalStatus.ACTIVE);
    }
}
