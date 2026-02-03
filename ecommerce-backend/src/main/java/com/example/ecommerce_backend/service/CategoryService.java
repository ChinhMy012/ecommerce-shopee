package com.example.ecommerce_backend.service;

import com.example.ecommerce_backend.dto.response.category.CategoryTreeResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryTreeResponse> getCategoryTree();
}
