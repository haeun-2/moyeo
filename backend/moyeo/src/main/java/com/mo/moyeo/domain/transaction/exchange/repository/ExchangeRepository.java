package com.mo.moyeo.domain.transaction.exchange.repository;

import com.mo.moyeo.domain.currency.entity.Currency;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.exchange.volume.dto.ExchangeVolumeGroupDto;
import com.mo.moyeo.domain.transaction.exchange.entity.ExchangeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface ExchangeRepository extends JpaRepository<ExchangeTransaction, Long> {

    @Query("SELECT e FROM ExchangeTransaction e WHERE e.transaction.id = :transactionId")
    List<ExchangeTransaction> findAllByTransactionId(@Param("transactionId") Long transactionId);

    @Query("""
    SELECT COALESCE(
        (SELECT SUM(e.fromAmount)
         FROM ExchangeTransaction e
         WHERE e.fromCurrency.code = :currencyType
           AND e.transaction.createdAt BETWEEN :startTime AND :endTime), 0
    )
    +
    COALESCE(
        (SELECT SUM(e.toAmount)
         FROM ExchangeTransaction e
         WHERE e.toCurrency.code = :currencyType
           AND e.transaction.createdAt BETWEEN :startTime AND :endTime), 0
    )
""")
    BigDecimal findVolumeByCurrencyAndTime(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("currencyType")CurrencyType currencyType
            );
}
