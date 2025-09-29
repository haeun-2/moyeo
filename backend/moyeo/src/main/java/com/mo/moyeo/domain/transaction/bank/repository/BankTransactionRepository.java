package com.mo.moyeo.domain.transaction.bank.repository;

import com.mo.moyeo.domain.transaction.bank.entity.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
}
