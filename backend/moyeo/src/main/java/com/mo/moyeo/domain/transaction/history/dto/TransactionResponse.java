package com.mo.moyeo.domain.transaction.history.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.transaction.history.entity.BoxHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long historyId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datetime;
    private String title;
    private Double amount;
    private CurrencyType currency;

    public static TransactionResponse from(BoxHistory boxHistory) {
        return TransactionResponse.builder()
                .historyId(boxHistory.getId())
                .datetime(boxHistory.getCreatedAt())
                .title(boxHistory.getTitle())
                .amount(boxHistory.getAmount())
                .currency(boxHistory.getCurrencyCode())
                .build();
    }

}