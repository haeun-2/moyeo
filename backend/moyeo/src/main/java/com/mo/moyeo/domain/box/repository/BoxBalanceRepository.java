package com.mo.moyeo.domain.box.repository;

import com.mo.moyeo.domain.box.entity.Box;
import com.mo.moyeo.domain.box.entity.BoxBalance;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoxBalanceRepository extends JpaRepository<BoxBalance, Long> {
    Optional<BoxBalance> findBoxBalanceByBoxAndCurrencyCode(Box box, CurrencyType currencyType);
}
