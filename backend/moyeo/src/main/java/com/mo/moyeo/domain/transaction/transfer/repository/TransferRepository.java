package com.mo.moyeo.domain.transaction.transfer.repository;

import com.mo.moyeo.domain.transaction.transfer.entity.TransferTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<TransferTransaction, Long> {
}
