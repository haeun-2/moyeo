package com.mo.moyeo.domain.exchange.volume.repository;

public interface ExchangeStatsProjection {
    String getPeriod();
    Double getTotalAmount();
}