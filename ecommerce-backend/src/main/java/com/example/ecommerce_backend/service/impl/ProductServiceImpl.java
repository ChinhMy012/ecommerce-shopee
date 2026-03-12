package com.example.ecommerce_backend.service.impl;

import com.example.ecommerce_backend.dto.request.image.ProductImageRequest;
import com.example.ecommerce_backend.dto.request.product.CreateProductRequest;
import com.example.ecommerce_backend.dto.request.product.VariantAttributeRequest;
import com.example.ecommerce_backend.dto.request.product.VariantRequest;
import com.example.ecommerce_backend.dto.response.product.*;
import com.example.ecommerce_backend.entity.*;
import com.example.ecommerce_backend.repository.*;
import com.example.ecommerce_backend.service.ProductService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantAttributeRepository productVariantAttributeRepository;
    private final ImageRepository imageRepository;


    private User getCurrentUser() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

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

        // COPY variants ra List mới
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

        // COPY product images
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

    @Override
    @Transactional
    public void createProduct(CreateProductRequest request) {

        User uAthu = getCurrentUser();

        // 1. tìm shop
        Shop shop = shopRepository.findByUser(uAthu)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        // 2. load category từ DB
        Set<Category> categories = request.getCategoryIds().stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Category not found: " + id)))
                .collect(Collectors.toSet());

        // 3. tạo product
        Product product = new Product();
        product.setName(request.getName());
        product.setSlug(generateUniqueSlug(request.getName()));
        product.setDescription(request.getDescription());
        product.setShop(shop);
        product.setCategories(categories);
        product.setStatus(Product.ProductStatus.PENDING);

        productRepository.save(product);

        // 4. save product images
        saveProductImages(product, request.getImages());

        // 5. save variants
        saveVariants(product, request.getVariants());
    }

    private String generateSlug(String name) {

        // bỏ dấu tiếng Việt
        String slug = java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // lowercase
        slug = slug.toLowerCase();

        // thay ký tự không hợp lệ thành -
        slug = slug.replaceAll("[^a-z0-9]+", "-");

        // bỏ - ở đầu và cuối
        slug = slug.replaceAll("^-|-$", "");

        return slug;
    }

    private String generateUniqueSlug(String name) {

        String baseSlug = generateSlug(name);
        String slug = baseSlug;
        int count = 1;

        while (productRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + count;
            count++;
        }

        return slug;
    }
    private void saveVariants(Product product, List<VariantRequest> variantRequests) {

        for (VariantRequest variantRequest : variantRequests) {

            ProductVariant variant = new ProductVariant();

            variant.setProduct(product);
            variant.setSku(variantRequest.getSku());
            variant.setPrice(variantRequest.getPrice());
            variant.setStock(variantRequest.getStock());
            variant.setStatus(ProductVariant.VariantStatus.ACTIVE);

            productVariantRepository.save(variant);

            saveVariantAttributes(variant, variantRequest.getAttributes());
        }
    }

    private void saveVariantAttributes(
            ProductVariant variant,
            List<VariantAttributeRequest> attributes
    ) {

        if (attributes == null) return;

        for (VariantAttributeRequest attr : attributes) {

            ProductVariantAttribute attribute = new ProductVariantAttribute();

            attribute.setVariant(variant);
            attribute.setAttributeName(attr.getAttributeName());
            attribute.setAttributeValue(attr.getAttributeValue());

            productVariantAttributeRepository.save(attribute);
        }
    }
    private void saveProductImages(Product product, List<ProductImageRequest> images) {

        if (images == null || images.isEmpty()) return;

        for (ProductImageRequest img : images) {

            Image image = new Image();
            image.setUrl(img.getUrl());
            image.setAltText(img.getAltText());
            image.setProduct(product);

            imageRepository.save(image);
        }
    }

    @Transactional
    public void updateStatus(Integer productId, Product.ProductStatus status) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStatus(status);
    }

    @Override
    public List<AdminProductReviewResponse> getPendingProducts() {
        return productRepository.findProductsByStatus("PENDING");
    }
}
