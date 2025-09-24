package com.mo.moyeo.domain.transaction.payment.dto;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequestDto(
        @NotNull
        String token,
        @NotNull
        Long merchantId,
        @NotNull
        CurrencyType currencyType,
        @NotNull @Min(0L)
        @DecimalMax(value = "3000000", message = "최대 금액 한도 초과")
        BigDecimal amount
) {
}
