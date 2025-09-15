package com.mo.moyeo.domain.exchange.reservation.dto;


import com.mo.moyeo.domain.currency.entity.CurrencyType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeReserveDto (
        Long boxId,
        CurrencyType fromCurrency,
        CurrencyType toCurrency,
        BigDecimal amount,
        BigDecimal targetRate,
        LocalDate expiresAt
){
}
