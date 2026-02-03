package com.example.ecommerce_backend.service.impl;

import com.example.ecommerce_backend.dto.response.ProductResponse;
import com.example.ecommerce_backend.dto.response.VariantResponse;
import com.example.ecommerce_backend.entity.Category;
import com.example.ecommerce_backend.entity.Product;
import com.example.ecommerce_backend.repository.ProductRepository;
import com.example.ecommerce_backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository
                .findAllByStatus(Product.ProductStatus.APPROVED)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ProductResponse mapToResponse(Product product) {
        ProductResponse res = new ProductResponse();
        res.setId(product.getId());
        res.setName(product.getName());
        res.setDescription(product.getDescription());

        // Categories
        res.setCategories(
                product.getCategories()
                        .stream()
                        .map(Category::getName)
                        .toList()
        );

        // Variants
        res.setVariants(
                product.getVariants()
                        .stream()
                        .map(v -> {
                            VariantResponse vr = new VariantResponse();
                            vr.setSku(v.getSku());
                            vr.setPrice(v.getPrice());
                            vr.setStock(v.getStock());
                            return vr;
                        })
                        .toList()
        );

        return res;
    }
}
