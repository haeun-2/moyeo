package com.mo.moyeo.domain.exchange.exchange_rate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ExchangeRateHistoryDto {
    Double buyRate;
    Double sellRate;
    Double originalRate;
    LocalDateTime recordedAt;
}
