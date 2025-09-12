package com.mo.moyeo.domain.transaction.bank.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class WithdrawRequest {

    @NotNull(message = "입금 금액은 필수입니다.")
    private Long balance;
}
