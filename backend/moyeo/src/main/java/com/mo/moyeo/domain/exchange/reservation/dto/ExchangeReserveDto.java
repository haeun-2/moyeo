package com.mo.moyeo.domain.exchange.reservation.dto;


import com.mo.moyeo.domain.currency.entity.CurrencyType;
import jakarta.validation.constraints.DecimalMax;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeReserveDto (
        Long boxId,
        CurrencyType fromCurrency,
        CurrencyType toCurrency,

        @DecimalMax(value = "3000000", message = "최대 금액 한도 초과")
        BigDecimal amount,
        
        BigDecimal targetRate,
        LocalDate expiresAt
){
}
