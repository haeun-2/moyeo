package com.mo.moyeo.domain.exchange.volume.repository;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.transaction.exchange.entity.ExchangeTransaction;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExchangeVolumeRepository extends JpaRepository<ExchangeTransaction, Long> {
    @Query(value = """
    SELECT 
        DATE_FORMAT(t.created_at, :timeFormat) AS period,
        SUM(
            CASE 
                WHEN et.from_currency_code = :currencyCode THEN et.from_amount
                WHEN et.to_currency_code = :currencyCode THEN et.to_amount
                ELSE 0
            END
        ) AS total_amount
    FROM exchange_transactions et
    JOIN transactions t ON et.transaction_id = t.transaction_id
    WHERE et.from_currency_code = :currencyCode OR et.to_currency_code = :currencyCode
    GROUP BY DATE_FORMAT(t.created_at, :timeFormat)
    ORDER BY period
""", nativeQuery = true)
    List<ExchangeStatsProjection> getStatisticsByCurrencyType(
            @Param("currencyCode") String currencyCode,
            @Param("timeFormat") String timeFormat
    );
}
