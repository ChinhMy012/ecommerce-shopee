package com.example.ecommerce_backend.controller;

import com.example.ecommerce_backend.dto.response.CategoryTreeResponse;
import com.example.ecommerce_backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    @PreAuthorize("hasAuthority('CATEGORY_VIEW')")
    @GetMapping("/tree")
    public List<CategoryTreeResponse> getTree() {
        return categoryService.getCategoryTree();
    }
}
