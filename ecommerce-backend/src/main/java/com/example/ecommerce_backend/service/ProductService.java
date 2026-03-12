package com.example.ecommerce_backend.service;

import com.example.ecommerce_backend.dto.request.product.CreateProductRequest;
import com.example.ecommerce_backend.dto.response.product.AdminProductReviewResponse;
import com.example.ecommerce_backend.dto.response.product.ProductDetailResponse;
import com.example.ecommerce_backend.dto.response.product.ProductResponse;
import com.example.ecommerce_backend.entity.Product;

import java.util.List;

public interface ProductService {

    List<ProductResponse> getAllProducts();

     ProductDetailResponse getProductDetail(String slug);

    void createProduct( CreateProductRequest request);

    void updateStatus(Integer productId, Product.ProductStatus status);

    List<AdminProductReviewResponse> getPendingProducts();
}
