package com.mo.moyeo.domain.transaction.category.dto;

import com.mo.moyeo.domain.transaction.category.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CategoryResponse {

    private Long categoryId;
    private String CategoryName;

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName().getLabel());
    }

    public static List<CategoryResponse> from(List<Category> categoryList) {
        return categoryList.stream().map(CategoryResponse::from).toList();
    }

}
