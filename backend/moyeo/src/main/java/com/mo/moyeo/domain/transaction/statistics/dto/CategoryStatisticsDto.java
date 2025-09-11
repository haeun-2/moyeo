package com.mo.moyeo.domain.transaction.statistics.dto;

import com.mo.moyeo.domain.transaction.category.entity.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class CategoryStatisticsDto {

    private Long categoryId;
    private CategoryType category;
    private Double amount;

}
