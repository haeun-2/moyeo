package com.mo.moyeo.domain.box.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.box.entity.Box;
import com.mo.moyeo.domain.box.entity.BoxBalance;
import com.mo.moyeo.domain.box.repository.BoxBalanceRepository;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoxBalanceService {
    private final BoxBalanceRepository boxBalanceRepository;

    public BoxBalance findBoxBalanceByBoxIdAndCurrencyType(Box box, CurrencyType currencyType) {
        return boxBalanceRepository.findBoxBalanceByBoxAndCurrencyCode(box, currencyType)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public void exchange(Box box, CurrencyType fromCurrency, CurrencyType toCurrency, Double fromAmount, Double toAmount) {
        BoxBalance fromBoxBalance = findBoxBalanceByBoxIdAndCurrencyType(box, fromCurrency);
        BoxBalance toBoxBalance = findBoxBalanceByBoxIdAndCurrencyType(box, toCurrency);

        if(fromBoxBalance.getBalance() < fromAmount)
            throw new CustomException(ErrorCode.BAD_REQUEST, "환전에 필요한 금액이 부족합니다.");

        fromBoxBalance.decreaseBalance(fromAmount);
        toBoxBalance.increaseBalance(toAmount);
    }
}
