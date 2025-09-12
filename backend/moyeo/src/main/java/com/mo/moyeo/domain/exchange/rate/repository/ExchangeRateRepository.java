package com.mo.moyeo.domain.exchange.rate.repository;

import com.mo.moyeo.domain.currency.entity.Currency;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.exchange.rate.dto.ExchangeRateHistoryDto;
import com.mo.moyeo.domain.exchange.rate.entity.ExchangeRate;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    void deleteByRecordedAtBefore(LocalDateTime dateTime);

    @Query(value = """
        SELECT
            DATE_FORMAT(er.recorded_at, :timeFormat) AS period,
            AVG(er.buy_rate) AS buyRate,
            AVG(er.sell_rate) AS sellRate,
            AVG(er.original_rate) AS originalRate
        FROM exchange_rates er
        WHERE er.currency_code = :currencyType
        GROUP BY DATE_FORMAT(er.recorded_at, :timeFormat)
        ORDER BY period
    """, nativeQuery = true)
    List<ExchangeRateProjection> getExchangeRateStatisticsByCurrency(String timeFormat,  CurrencyType currencyType);
}
