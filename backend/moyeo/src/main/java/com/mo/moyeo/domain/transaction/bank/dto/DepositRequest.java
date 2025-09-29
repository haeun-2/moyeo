package com.mo.moyeo.domain.transaction.bank.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class DepositRequest {

    @NotNull(message = "입금 금액은 필수입니다.")
    @DecimalMax(value = "3000000", message = "최대 금액 한도 초과")
    private BigDecimal balance;
}
