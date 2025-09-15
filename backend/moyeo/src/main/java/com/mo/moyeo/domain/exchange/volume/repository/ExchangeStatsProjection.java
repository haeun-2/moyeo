package com.mo.moyeo.domain.exchange.volume.repository;

import java.math.BigDecimal;

public interface ExchangeStatsProjection {
    String getPeriod();
    BigDecimal getTotalAmount();
}