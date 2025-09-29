package com.mo.moyeo.domain.transaction.transaction.repository;

import com.mo.moyeo.domain.transaction.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
