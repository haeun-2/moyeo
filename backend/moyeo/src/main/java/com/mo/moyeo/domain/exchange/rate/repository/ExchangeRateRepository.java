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
        er.buy_rate AS buyRate,
        er.sell_rate AS sellRate,
        er.original_rate AS originalRate,
        er.recorded_at AS period
    FROM exchange_rates er
    WHERE er.currency_code = :currencyType
""", nativeQuery = true)
    List<ExchangeRateProjection> getHistoryBy10m(@Param("currencyType") String currencyType);

    @Query(value = """
    SELECT 
        AVG(er.buy_rate) AS buyRate,
        AVG(er.sell_rate) AS sellRate,
        AVG(er.original_rate) AS originalRate,
        DATE_FORMAT(er.recorded_at, '%Y-%m-%d %H:00') AS period
    FROM exchange_rates er
    WHERE er.currency_code = :currencyType
    GROUP BY DATE_FORMAT(er.recorded_at, '%Y-%m-%d %H:00')
    ORDER BY period
""", nativeQuery = true)
    List<ExchangeRateProjection> getHistoryBy1h(@Param("currencyType") String currencyType);

    @Query(value = """
    SELECT 
        AVG(er.buy_rate) AS buyRate,
        AVG(er.sell_rate) AS sellRate,
        AVG(er.original_rate) AS originalRate,
        DATE_FORMAT(er.recorded_at, '%Y-%m-%d') AS period
    FROM exchange_rates er
    WHERE er.currency_code = :currencyType
    GROUP BY DATE_FORMAT(er.recorded_at, '%Y-%m-%d')
    ORDER BY period
""", nativeQuery = true)
    List<ExchangeRateProjection> getHistoryBy1d(@Param("currencyType") String currencyType);

}
