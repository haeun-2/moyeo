package com.mo.moyeo.domain.exchange.rate.dto;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.exchange.rate.entity.ExchangeRate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentExchangeRateDto {
    private CurrencyType currencyCode;
    private double buyRate;
    private double sellRate;
    private double originalRate;
    private String countryFlag;

    public CurrentExchangeRateDto(ExchangeRate exchangeRate) {
        this.currencyCode = exchangeRate.getCurrency().getCode();
        this.buyRate = exchangeRate.getBuyRate();
        this.sellRate = exchangeRate.getSellRate();
        this.originalRate = exchangeRate.getOriginalRate();
        this.countryFlag = currencyCode.getFlagEmoji();
    }
}
