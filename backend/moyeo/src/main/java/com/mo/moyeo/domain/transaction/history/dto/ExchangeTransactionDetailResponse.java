package com.mo.moyeo.domain.transaction.history.dto;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.transaction.exchange.entity.ExchangeTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ExchangeTransactionDetailResponse {

    private CurrencyType fromCurrency;
    private BigDecimal fromAmount;
    private CurrencyType toCurrency;
    private BigDecimal toAmount;
    private BigDecimal exchangeRate;

    public static ExchangeTransactionDetailResponse from(ExchangeTransaction exchangeTransaction) {
        return ExchangeTransactionDetailResponse.builder()
                .fromCurrency(exchangeTransaction.getFromCurrency().getCode())
                .fromAmount(exchangeTransaction.getFromAmount())
                .toCurrency(exchangeTransaction.getToCurrency().getCode())
                .toAmount(exchangeTransaction.getToAmount())
                .exchangeRate(exchangeTransaction.getExchangeRate())
                .build();
    }

    public static List<ExchangeTransactionDetailResponse> from(List<ExchangeTransaction> exchangeTransactions) {
        return exchangeTransactions.stream().map(ExchangeTransactionDetailResponse::from).toList();
    }

}
