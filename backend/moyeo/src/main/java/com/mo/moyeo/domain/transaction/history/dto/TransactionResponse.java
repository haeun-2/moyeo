package com.mo.moyeo.domain.transaction.history.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.transaction.category.entity.Category;
import com.mo.moyeo.domain.transaction.category.entity.CategoryType;
import com.mo.moyeo.domain.transaction.history.entity.BoxHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long historyId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datetime;
    private String title;
    private BigDecimal amount;
    private BigDecimal balance;
    private CurrencyType currency;
    private String memo;
    private String category;
    private String transactionType;

    public static TransactionResponse from(BoxHistory boxHistory) {
        return TransactionResponse.builder()
                .historyId(boxHistory.getId())
                .datetime(boxHistory.getCreatedAt())
                .title(boxHistory.getTitle())
                .amount(boxHistory.getAmount())
                .balance(boxHistory.getTotalAmount())
                .currency(boxHistory.getCurrencyCode())
                .memo(boxHistory.getMemo())
                .category(
                        Optional.ofNullable(boxHistory.getCategory())
                                .map(Category::getName)
                                .map(CategoryType::getLabel)
                                .orElse("")
                )
                .transactionType(boxHistory.getType().getLabel())
                .build();
    }

}