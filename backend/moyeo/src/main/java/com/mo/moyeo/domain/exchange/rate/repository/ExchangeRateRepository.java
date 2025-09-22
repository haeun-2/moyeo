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
        SELECT NEW com.example.dto.ExchangeRateDto(
                FUNCTION('DATE_FORMAT', er.recordedAt, '%Y-%m-%d %H:%i'),\s
                AVG(er.buyRate),\s
                AVG(er.sellRate),\s
                AVG(er.originalRate)
            )
            FROM ExchangeRate er
            WHERE er.currencyCode = :currencyType
            GROUP BY FUNCTION('DATE_FORMAT', er.recordedAt, '%Y-%m-%d %H:%i')
            ORDER BY period
    """, nativeQuery = true)
    List<ExchangeRateHistoryDto> getHistoryBy10m(CurrencyType currencyType);

    @Query(value = """
        SELECT
            DATE_FORMAT(er.recorded_at, '%Y-%m-%d %H:00:00') AS period,
            AVG(er.buy_rate) AS buyRate,
            AVG(er.sell_rate) AS sellRate,
            AVG(er.original_rate) AS originalRate
        FROM exchange_rates er
        WHERE er.currency_code = :currencyType
        GROUP BY DATE_FORMAT(er.recorded_at, '%Y-%m-%d %H:00:00')
        ORDER BY period
    """, nativeQuery = true)
    List<ExchangeRateProjection> getHistoryBy1h(CurrencyType currencyType);

    @Query(value = """
        SELECT
            DATE_FORMAT(er.recorded_at, '%Y-%m-%d') AS period,
            AVG(er.buy_rate) AS buyRate,
            AVG(er.sell_rate) AS sellRate,
            AVG(er.original_rate) AS originalRate
        FROM exchange_rates er
        WHERE er.currency_code = :currencyType
        GROUP BY DATE_FORMAT(er.recorded_at, '%Y-%m-%d')
        ORDER BY period
    """, nativeQuery = true)
    List<ExchangeRateProjection> getHistoryBy1d(CurrencyType currencyType);
}
