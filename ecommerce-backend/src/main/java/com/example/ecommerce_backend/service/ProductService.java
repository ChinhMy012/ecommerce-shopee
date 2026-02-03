package com.example.ecommerce_backend.service;

import com.example.ecommerce_backend.dto.response.ProductResponse;
import com.example.ecommerce_backend.entity.Product;

import java.util.List;

public interface ProductService {

    List<ProductResponse> getAllProducts();
}
