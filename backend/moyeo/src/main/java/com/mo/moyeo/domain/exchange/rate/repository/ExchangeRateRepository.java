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

//    @Query("""
//        SELECT NEW com.mo.moyeo.domain.exchange.rate.dto.ExchangeRateHistoryDto(
//            AVG(er.buyRate),
//            AVG(er.sellRate),
//            AVG(er.originalRate),
//            FUNCTION('DATE_FORMAT', er.recordedAt, '%Y-%m-%d %H:%i')
//        )
//        FROM ExchangeRate er
//        WHERE er.currency = :currencyType
//        GROUP BY FUNCTION('DATE_FORMAT', er.recordedAt, '%Y-%m-%d %H:%i')
//        ORDER BY FUNCTION('DATE_FORMAT', er.recordedAt, '%Y-%m-%d %H:%i')
//    """)
//    List<ExchangeRateHistoryDto> getHistoryBy10m(CurrencyType currencyType);

}
