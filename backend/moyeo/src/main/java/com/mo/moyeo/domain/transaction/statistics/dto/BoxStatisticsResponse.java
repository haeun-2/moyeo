package com.mo.moyeo.domain.transaction.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class BoxStatisticsResponse {

    private BigDecimal totalAmount;
    private List<CategoryStatistics> content;

    @ToString
    @Getter
    @AllArgsConstructor
    @Builder
    public static class CategoryStatistics {
        private Long categoryId;
        private String category;
        private BigDecimal amount;
        private BigDecimal ratio;

        public static CategoryStatistics from(CategoryStatisticsDto dto, BigDecimal totalAmount) {
            return CategoryStatistics.builder()
                    .categoryId(dto.getCategoryId())
                    .category(dto.getCategory().getLabel())
                    .amount(dto.getAmount())
                    .ratio(dto.getAmount().divide(totalAmount, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))) // BigDecimal 나눗셈
                    .build();
        }
    }

    public static BoxStatisticsResponse from(List<CategoryStatisticsDto> dtoList) {
        BigDecimal totalAmount = dtoList.stream()
                .map(CategoryStatisticsDto::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // BigDecimal 합산

        return BoxStatisticsResponse.builder()
                .totalAmount(totalAmount)
                .content(dtoList.stream()
                        .map(dto -> CategoryStatistics.from(dto, totalAmount))
                        .toList())
                .build();
    }

}

