package com.mo.moyeo.domain.transaction.category.service;

import com.mo.moyeo.domain.transaction.category.dto.CategoryResponse;
import com.mo.moyeo.domain.transaction.category.entity.Category;
import com.mo.moyeo.domain.transaction.category.entity.CategoryType;
import com.mo.moyeo.domain.transaction.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final String CATEGORY_KEY = "category";

    @Cacheable(value = CATEGORY_KEY, key = "'all'")
    public Map<CategoryType, Category> getAllCategoryMap() {
        return categoryRepository.findAll().stream()
                .collect(Collectors.toMap(Category::getName, category -> category));
    }

}
