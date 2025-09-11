package com.mo.moyeo.domain.exchange_rate.repository;

import com.mo.moyeo.domain.currency.entity.Currency;
import com.mo.moyeo.domain.exchange_rate.dto.ExchangeRateHistoryDto;
import com.mo.moyeo.domain.exchange_rate.entity.ExchangeRate;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    void deleteByRecordedAtBefore(LocalDateTime dateTime);

    @Query("""
            select new com.mo.moyeo.domain.exchange_rate.dto.ExchangeRateHistoryDto(
                er.buyRate, er.sellRate, er.originalRate, er.recordedAt
            )
            from exchange_rates er
            where er.currency= :currency
            order by er.recordedAt asc
            """)
    List<ExchangeRateHistoryDto> getExchangeRateByCurrency(@Param("currency") Currency currency);
}
