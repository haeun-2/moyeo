package com.mo.moyeo.domain.exchange.volume.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ExchangeVolumeProjection {
    LocalDateTime getRecordedAt();
    BigDecimal getAmount();
}