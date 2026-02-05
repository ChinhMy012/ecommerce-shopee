package com.example.ecommerce_backend.service.impl;

import com.example.ecommerce_backend.dto.response.product.AttributeResponse;
import com.example.ecommerce_backend.dto.response.product.ProductDetailResponse;
import com.example.ecommerce_backend.dto.response.product.ProductResponse;
import com.example.ecommerce_backend.dto.response.product.VariantResponse;
import com.example.ecommerce_backend.entity.*;
import com.example.ecommerce_backend.repository.ProductRepository;
import com.example.ecommerce_backend.service.ProductService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

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
        res.setSlug(product.getSlug());
        res.setMinPrice(
                product.getVariants()
                        .stream()
                        .filter(v -> v.getStatus() == ProductVariant.VariantStatus.ACTIVE)
                        .map(ProductVariant::getPrice)          // BigDecimal
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO)
        );
        res.setMaxPrice(product.getVariants()
                .stream()
                .filter(v -> v.getStatus() == ProductVariant.VariantStatus.ACTIVE)
                .map(ProductVariant::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO));
        res.setThumbnail(product.getImages()
                .stream()
                .findFirst()
                .map(Image::getUrl)
                .orElse(null));

        // Categories
        res.setCategories(
                product.getCategories()
                        .stream()
                        .map(Category::getName)
                        .toList()
        );



        return res;
    }
    @Override
    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(String slug) {

        Product product = productRepository
                .findBySlugAndStatus(slug, Product.ProductStatus.APPROVED)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Map<String, Set<String>> attributeMap = new LinkedHashMap<>();
        List<VariantResponse> variantResponses = new ArrayList<>();

        // ✅ COPY variants ra List mới
        List<ProductVariant> variants = new ArrayList<>(product.getVariants());

        for (ProductVariant variant : variants) {

            if (variant.getStatus() != ProductVariant.VariantStatus.ACTIVE) {
                continue;
            }

            VariantResponse vr = new VariantResponse();
            vr.setId(variant.getId());
            vr.setSku(variant.getSku());
            vr.setPrice(variant.getPrice());
            vr.setStock(variant.getStock());

            // ======================
            // Variant attributes
            // ======================
            Map<String, String> attrs = new LinkedHashMap<>();

            List<ProductVariantAttribute> attributes =
                    new ArrayList<>(variant.getAttributes());

            for (ProductVariantAttribute attr : attributes) {
                attrs.put(attr.getAttributeName(), attr.getAttributeValue());

                attributeMap
                        .computeIfAbsent(attr.getAttributeName(), k -> new LinkedHashSet<>())
                        .add(attr.getAttributeValue());
            }
            vr.setAttributes(attrs);

            // ======================
            // Variant images
            // ======================
            List<Image> images = new ArrayList<>(variant.getImages());

            vr.setImages(
                    images.stream()
                            .map(Image::getUrl)
                            .toList()
            );

            variantResponses.add(vr);
        }

        // ======================
        // AttributeResponse
        // ======================
        List<AttributeResponse> attributeResponses = attributeMap.entrySet()
                .stream()
                .map(e -> {
                    AttributeResponse ar = new AttributeResponse();
                    ar.setName(e.getKey());
                    ar.setValues(new ArrayList<>(e.getValue()));
                    return ar;
                })
                .toList();

        // ======================
        // ProductDetailResponse
        // ======================
        ProductDetailResponse res = new ProductDetailResponse();
        res.setId(product.getId());
        res.setName(product.getName());
        res.setDescription(product.getDescription());
        res.setSlug(product.getSlug());

        // ✅ COPY product images
        List<Image> productImages = new ArrayList<>(product.getImages());

        res.setImages(
                productImages.stream()
                        .map(Image::getUrl)
                        .toList()
        );

        res.setAttributes(attributeResponses);
        res.setVariants(variantResponses);

        return res;
    }
}
