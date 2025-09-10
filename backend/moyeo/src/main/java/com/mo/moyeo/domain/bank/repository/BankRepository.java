package com.mo.moyeo.domain.bank.repository;

import com.mo.moyeo.domain.bank.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank, String> {

}