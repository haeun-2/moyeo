package com.mo.moyeo.domain.exchange.rate.dto;

import com.mo.moyeo.domain.exchange.rate.repository.ExchangeRateProjection;
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
    String period;

}
