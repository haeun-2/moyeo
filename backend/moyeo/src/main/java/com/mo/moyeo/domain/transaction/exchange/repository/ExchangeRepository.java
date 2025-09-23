package com.mo.moyeo.domain.transaction.exchange.repository;

import com.mo.moyeo.domain.currency.entity.Currency;
import com.mo.moyeo.domain.exchange.volume.dto.ExchangeVolumeGroupDto;
import com.mo.moyeo.domain.transaction.exchange.entity.ExchangeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface ExchangeRepository extends JpaRepository<ExchangeTransaction, Long> {

    @Query("SELECT e FROM ExchangeTransaction e WHERE e.transaction.id = :transactionId")
    List<ExchangeTransaction> findAllByTransactionId(@Param("transactionId") Long transactionId);

    @Query(
            value = """
        SELECT
              c.currency_code,
              COALESCE(SUM(sub.amount), 0) AS amount
          FROM currencies c
          LEFT JOIN (
              SELECT from_currency_code AS currency_code, from_amount AS amount
              FROM exchange_transactions et
              JOIN transactions t ON et.transaction_id = t.transaction_id
              WHERE t.created_at BETWEEN :startTime AND :endTime
          
              UNION ALL
          
              SELECT to_currency_code AS currency_code, to_amount AS amount
              FROM exchange_transactions et
              JOIN transactions t ON et.transaction_id = t.transaction_id
              WHERE t.created_at BETWEEN :startTime AND :endTime
          ) AS sub ON c.currency_code = sub.currency_code
          WHERE c.currency_code <> 'KRW'
          GROUP BY c.currency_code
          ORDER BY c.currency_code;
    """,
            nativeQuery = true
    )
    List<ExchangeVolumeGroupDto> findVolumeByCurrency(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
