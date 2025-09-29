package com.mo.moyeo.domain.exchange.volume.dto;

import com.mo.moyeo.domain.currency.entity.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeVolumeGroupDto {
    private String currencyCode;
    private BigDecimal amount;
}