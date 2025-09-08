package com.mo.moyeo.domain.exchange.exchange_rate.dto;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.exchange.exchange_rate.entity.ExchangeRate;
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
    private double minExchange;
    private String countryFlag;

    public CurrentExchangeRateDto(ExchangeRate exchangeRate) {
        this.currencyCode = exchangeRate.getCurrency().getCode();
        this.buyRate = exchangeRate.getBuyRate();
        this.sellRate = exchangeRate.getSellRate();
        this.minExchange = exchangeRate.getExchangeMin();
        this.countryFlag = currencyCode.getFlagEmoji();
    }
}
