package com.mo.moyeo.domain.currency.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.currency.dto.FavoriteCurrencyRequest;
import com.mo.moyeo.domain.currency.entity.Currency;
import com.mo.moyeo.domain.currency.entity.FavoriteCurrency;
import com.mo.moyeo.domain.currency.repository.FavoriteCurrencyRepository;
import com.mo.moyeo.domain.exchange.rate.dto.CurrentExchangeRateDto;
import com.mo.moyeo.domain.exchange.rate.service.ExchangeRateCacheService;
import com.mo.moyeo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteCurrencyService {

    private final FavoriteCurrencyRepository favoriteCurrencyRepository;
    private final CurrencyService currencyService;
    private final ExchangeRateCacheService exchangeRateCacheService;

    @Transactional
    public void likeCurrency(User user, FavoriteCurrencyRequest favoriteCurrencyRequest) {

        Currency currency = currencyService.getReferenceByType(favoriteCurrencyRequest.getCurrencyCode());

        if(favoriteCurrencyRepository.existsByCurrencyAndUser(currency, user)) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE, "이미 관심 통화로 등록되어 있습니다.");
        }

        FavoriteCurrency favoriteCurrency = FavoriteCurrency.builder()
                .user(user)
                .currency(currency)
                .build();

        favoriteCurrencyRepository.save(favoriteCurrency);
    }

    @Transactional
    public void unlikeCurrency(User user, FavoriteCurrencyRequest favoriteCurrencyRequest) {

        Currency currency = currencyService.getReferenceByType(favoriteCurrencyRequest.getCurrencyCode());

        FavoriteCurrency favoriteCurrency = favoriteCurrencyRepository.findByCurrencyAndUser(currency, user)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));

        favoriteCurrencyRepository.delete(favoriteCurrency);
    }

    public List<CurrentExchangeRateDto> getAllFavoriteCurrencies(User user) {

        List<FavoriteCurrency> favoriteCurrencies = favoriteCurrencyRepository.findAllByUser(user);
        Map<String, CurrentExchangeRateDto> currentExchangeRate = exchangeRateCacheService.getCurrentExchangeRate();

        return favoriteCurrencies.stream()
                .map(favoriteCurrency -> currentExchangeRate.get(
                        favoriteCurrency.getCurrency().getCode().name()
                ))
                .filter(Objects::nonNull) // null 값 제거
                .toList();
    }
}
