package com.mo.moyeo.domain.transaction.exchange.repository;

import com.mo.moyeo.domain.transaction.exchange.entity.ExchangeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRepository extends JpaRepository<ExchangeTransaction, Long> {
}
