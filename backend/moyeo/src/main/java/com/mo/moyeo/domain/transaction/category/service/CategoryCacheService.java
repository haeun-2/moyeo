package com.mo.moyeo.domain.transaction.category.service;

import com.mo.moyeo.domain.transaction.category.dto.CategoryResponse;
import com.mo.moyeo.domain.transaction.category.entity.Category;
import com.mo.moyeo.domain.transaction.category.entity.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryCacheService {

    private final CategoryService categoryService;

    public Category getByName(CategoryType type) {
        return categoryService.getAllCategoryMap().get(type);
    }

    public List<CategoryResponse> getAllCategoryList() {
        return categoryService.getAllCategoryMap().values().stream()
                .sorted(Comparator.comparing(Category::getId)) // ID 오름차순 정렬
                .map(CategoryResponse::from)
                .toList();
    }


}
