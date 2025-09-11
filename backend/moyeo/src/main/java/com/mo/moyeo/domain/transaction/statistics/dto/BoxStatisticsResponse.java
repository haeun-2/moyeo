package com.mo.moyeo.domain.transaction.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class BoxStatisticsResponse {

    private Double totalAmount;
    private List<CategoryStatistics> content;

    @ToString
    @Getter
    @AllArgsConstructor
    @Builder
    public static class CategoryStatistics {
        private Long categoryId;
        private String category;
        private Double amount;
        private Double ratio;

        public static CategoryStatistics from(CategoryStatisticsDto dto, Double totalAmount) {
            return CategoryStatistics.builder()
                    .categoryId(dto.getCategoryId())
                    .category(dto.getCategory().getLabel())
                    .amount(dto.getAmount())
                    .ratio(dto.getAmount() / totalAmount)
                    .build();
        }
    }

    public static BoxStatisticsResponse from(List<CategoryStatisticsDto> dtoList) {
        Double totalAmount = dtoList.stream()
                .map(CategoryStatisticsDto::getAmount)
                .reduce(0d, Double::sum);

        return BoxStatisticsResponse.builder()
                .totalAmount(totalAmount)
                .content(dtoList.stream().map(dto -> CategoryStatistics.from(dto, totalAmount)).toList())
                .build();
    }

}
