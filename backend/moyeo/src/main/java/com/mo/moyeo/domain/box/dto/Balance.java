package com.mo.moyeo.domain.box.dto;

import com.mo.moyeo.domain.box.entity.BoxBalance;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Getter
public class Balance {
    private CurrencyType currency;
    private BigDecimal balance;

    public static Balance from(BoxBalance boxBalance) {
        return Balance.builder()
                .currency(boxBalance.getCurrencyCode())
                .balance(boxBalance.getBalance())
                .build();
    }

    public static List<Balance> from(List<BoxBalance> boxBalances) {
        return boxBalances.stream().map(Balance::from).collect(Collectors.toList());
    }

}
