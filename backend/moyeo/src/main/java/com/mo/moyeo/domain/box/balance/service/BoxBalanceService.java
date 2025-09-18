package com.mo.moyeo.domain.box.balance.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.common.util.batch.BatchInsert;
import com.mo.moyeo.domain.box.box.entity.Box;
import com.mo.moyeo.domain.box.balance.entity.BoxBalance;
import com.mo.moyeo.domain.box.balance.repository.BoxBalanceRepository;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoxBalanceService {
    private final BoxBalanceRepository boxBalanceRepository;
    private final BatchInsert batchInsert;

    public BoxBalance findBoxBalanceByBoxAndCurrencyType(Box box, CurrencyType currencyType) {
        return boxBalanceRepository.findBoxBalanceByBoxAndCurrencyCode(box, currencyType)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public void exchange(Box box, CurrencyType fromCurrency, CurrencyType toCurrency, BigDecimal fromAmount, BigDecimal toAmount) {
        BoxBalance fromBoxBalance = findBoxBalanceByBoxAndCurrencyType(box, fromCurrency);
        BoxBalance toBoxBalance = findBoxBalanceByBoxAndCurrencyType(box, toCurrency);

        if(fromBoxBalance.checkSufficientBalance(fromAmount))
            throw new CustomException(ErrorCode.BAD_REQUEST, "환전에 필요한 금액이 부족합니다.");

        fromBoxBalance.decreaseBalance(fromAmount);
        toBoxBalance.increaseBalance(toAmount);
    }

    public void saveAllBoxBalance(List<BoxBalance> boxBalances) {
        batchInsert.saveBatch(boxBalances);
    }
}
