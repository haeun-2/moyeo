package com.mo.moyeo.domain.transaction.category.controller;

import com.mo.moyeo.domain.transaction.category.dto.CategoryResponse;
import com.mo.moyeo.domain.transaction.category.service.CategoryCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryCacheService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategory() {
        List<CategoryResponse> response = categoryService.getAllCategoryList();
        return ResponseEntity.ok(response);
    }

}
