package com.mo.moyeo.domain.transaction.category.service;

import com.mo.moyeo.domain.transaction.category.entity.Category;
import com.mo.moyeo.domain.transaction.category.entity.CategoryType;
import com.mo.moyeo.domain.transaction.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category getByName(CategoryType type) {
        return categoryRepository.findByName(type);
    }

}
