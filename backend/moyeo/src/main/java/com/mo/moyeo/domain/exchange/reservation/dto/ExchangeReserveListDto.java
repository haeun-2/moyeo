package com.mo.moyeo.domain.exchange.reservation.dto;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.exchange.reservation.entity.ReservedExchange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeReserveListDto {
    private Long id;
    private CurrencyType fromCurrency;
    private CurrencyType toCurrency;
    private double targetRate;
    private double amount;
    private LocalDate expiresAt;
    private LocalDateTime createdAt;
    private ReservedExchange.Status status;

}
