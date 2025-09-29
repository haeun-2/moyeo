package com.mo.moyeo.domain.exchange.rate.repository;

import java.math.BigDecimal;

public interface ExchangeRateProjection {
    BigDecimal getBuyRate();
    BigDecimal getSellRate();
    BigDecimal getOriginalRate();
    String getPeriod();
}
