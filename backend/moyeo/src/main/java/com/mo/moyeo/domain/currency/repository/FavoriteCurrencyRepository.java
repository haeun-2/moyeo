package com.mo.moyeo.domain.currency.repository;

import com.mo.moyeo.domain.currency.entity.Currency;
import com.mo.moyeo.domain.currency.entity.FavoriteCurrency;
import com.mo.moyeo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteCurrencyRepository extends JpaRepository<FavoriteCurrency, Long> {

    Optional<FavoriteCurrency> findByCurrencyAndUser(Currency currency, User user);

    Boolean existsByCurrencyAndUser(Currency currency, User user);

    List<FavoriteCurrency> findAllByUser(User user);
}
