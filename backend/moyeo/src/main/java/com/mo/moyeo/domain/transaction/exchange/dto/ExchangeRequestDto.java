package com.mo.moyeo.domain.transaction.exchange.dto;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExchangeRequestDto {
    @NotEmpty(message = "어떤 박스에서 출금할지 선택해주세요.")
    private Long fromBoxId;
    @NotEmpty(message = "환전하는 통화를 선택해주세요.")
    private CurrencyType fromCurrency;
    @NotEmpty(message = "환전할 통화를 선택해주세요.")
    private CurrencyType toCurrency;
    @NotEmpty(message = "금액을 입력해 주세요.")
    @Min(value = 0, message = "0보다 큰 수를 입력해주세요.")
    private Double amount;
}
