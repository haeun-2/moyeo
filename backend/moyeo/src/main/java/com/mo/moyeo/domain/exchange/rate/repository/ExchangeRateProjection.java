package com.mo.moyeo.domain.exchange.rate.repository;

public interface ExchangeRateProjection {
    Double getBuyRate();
    Double getSellRate();
    Double getOriginalRate();
    String getPeriod();
}
