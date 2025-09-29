package com.mo.moyeo.domain.transaction.payment.repository;

import com.mo.moyeo.domain.transaction.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
