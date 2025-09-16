package com.mo.moyeo.domain.box.box.dto;

import com.mo.moyeo.domain.box.balance.entity.BoxBalance;
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
public class BalanceDto {
    private CurrencyType currency;
    private BigDecimal balance;

    public static BalanceDto from(BoxBalance boxBalance) {
        return BalanceDto.builder()
                .currency(boxBalance.getCurrencyCode())
                .balance(boxBalance.getBalance())
                .build();
    }

    public static List<BalanceDto> from(List<BoxBalance> boxBalances) {
        return boxBalances.stream().map(BalanceDto::from).collect(Collectors.toList());
    }

}
