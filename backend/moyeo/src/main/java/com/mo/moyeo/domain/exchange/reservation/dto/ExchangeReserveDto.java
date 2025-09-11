package com.mo.moyeo.domain.exchange.reservation.dto;


import com.mo.moyeo.domain.currency.entity.CurrencyType;

import java.time.LocalDate;

public record ExchangeReserveDto (
        Long boxId,
        CurrencyType fromCurrency,
        CurrencyType toCurrency,
        double amount,
        double targetRate,
        LocalDate expiresAt
){
}
