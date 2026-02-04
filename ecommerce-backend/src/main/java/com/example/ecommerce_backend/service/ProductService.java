package com.example.ecommerce_backend.service;

import com.example.ecommerce_backend.dto.response.product.ProductDetailResponse;
import com.example.ecommerce_backend.dto.response.product.ProductResponse;

import java.util.List;

public interface ProductService {

    List<ProductResponse> getAllProducts();

    public ProductDetailResponse getProductDetail(String slug);
}
