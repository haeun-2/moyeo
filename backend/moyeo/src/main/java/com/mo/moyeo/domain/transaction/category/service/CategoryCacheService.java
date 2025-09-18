package com.mo.moyeo.domain.transaction.category.service;

import com.mo.moyeo.domain.transaction.category.dto.CategoryResponse;
import com.mo.moyeo.domain.transaction.category.entity.Category;
import com.mo.moyeo.domain.transaction.category.entity.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryCacheService {

    private final CategoryService categoryService;

    public Category getByName(CategoryType type) {
        return categoryService.getAllCategoryMap().get(type.name());
    }

    public List<CategoryResponse> getAllCategoryList() {
        return categoryService.getAllCategoryMap().values().stream()
                .map(CategoryResponse::from)
                .toList();
    }

}
