package com.mo.moyeo.domain.currency.dto;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FavoriteCurrencyRequest {

    private CurrencyType currencyCode;
}
