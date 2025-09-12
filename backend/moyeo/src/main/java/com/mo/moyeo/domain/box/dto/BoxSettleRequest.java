package com.mo.moyeo.domain.box.dto;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoxSettleRequest {

    @NotNull(message = "정산할 멤버가 필요합니다.")
    private Long boxMemberId;

    @NotNull(message = "정산 금액은 필수 입력값입니다.")
    private Double amount;

    @NotNull(message = "정산할 통화가 필요합니다.")
    private CurrencyType currencyType;

}
