package com.mo.moyeo.domain.transaction.exchange.repository;

import com.mo.moyeo.domain.transaction.exchange.entity.ExchangeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRepository extends JpaRepository<ExchangeTransaction, Long> {

    @Query("SELECT e FROM exchange_transactions e WHERE e.transaction.id = :transactionId")
    List<ExchangeTransaction> findAllByTransactionId(@Param("transactionId") Long transactionId);

}
