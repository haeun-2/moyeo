package com.mo.moyeo.domain.currency.service;

import com.mo.moyeo.domain.currency.dto.CurrencyListDto;
import com.mo.moyeo.domain.currency.entity.Currency;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.currency.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyRepository currencyRepository;

    public List<CurrencyListDto> getCurrencyList() {
        List<Currency> currencyList = currencyRepository.findAll();

        return currencyList.stream()
                .filter(c -> !c.getCode().name().equals("KRW"))
                .map(c -> {
                    return new CurrencyListDto(
                            c.getCode(),
                            c.getCountryName(),
                            c.getCurrencyUnit(),
                            c.getCountryFlag()
                    );
                }).toList();
    }

    public Currency getReferenceByType(CurrencyType currencyType) {
        return currencyRepository.getReferenceById(currencyType);
    }
}
