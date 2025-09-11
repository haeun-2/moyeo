package com.mo.moyeo.domain.currency.service;

import com.mo.moyeo.domain.currency.dto.CurrencyListDto;
import com.mo.moyeo.domain.currency.entity.Currency;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.currency.repository.CurrencyRepository;
import com.mo.moyeo.domain.exchange_rate.dto.CurrentExchangeRateDto;
import com.mo.moyeo.domain.exchange_rate.service.ExchangeRateCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final ExchangeRateCacheService exchangeRateCacheService;

    public List<CurrencyListDto> getCurrencyList() {
        List<Currency> currencyList = currencyRepository.findAll();
        Map<String, CurrentExchangeRateDto> current = exchangeRateCacheService.getCurrentExchangeRate();

        return currencyList.stream()
                .filter(c -> !c.getCode().name().equals("KRW"))
                .map(c -> {
                    return new CurrencyListDto(
                            c.getCode(),
                            c.getCountryName(),
                            c.getCurrencyUnit(),
                            c.getCountryFlag(),
                            current.get(c.getCode().name()).getMinExchange()
                    );
                }).toList();
    }

    public Currency getReferenceByType(CurrencyType currencyType){
        return currencyRepository.getReferenceById(currencyType);
    }
}
