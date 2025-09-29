package com.mo.moyeo.domain.exchange.rate.dto;

import com.mo.moyeo.domain.exchange.rate.repository.ExchangeRateProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExchangeRateHistoryDto {
    BigDecimal buyRate;
    BigDecimal sellRate;
    BigDecimal originalRate;
    String period;

}
