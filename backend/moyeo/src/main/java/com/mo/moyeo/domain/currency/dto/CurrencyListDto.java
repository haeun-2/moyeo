package com.mo.moyeo.domain.currency.dto;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyListDto {
    private CurrencyType code;
    private String countryName;
    private String currencyUnit;
    private String countryFlag;
    private double exchangeMin;
}
