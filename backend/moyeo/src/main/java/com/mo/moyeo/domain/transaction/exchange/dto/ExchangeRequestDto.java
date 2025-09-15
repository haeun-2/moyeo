package com.mo.moyeo.domain.transaction.exchange.dto;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.exchange.reservation.entity.ReservedExchange;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExchangeRequestDto {
    @NotNull(message = "어떤 박스에서 출금할지 선택해주세요.")
    private Long fromBoxId;
    @NotNull(message = "환전하는 통화를 선택해주세요.")
    private CurrencyType fromCurrency;
    @NotNull(message = "환전할 통화를 선택해주세요.")
    private CurrencyType toCurrency;
    @NotNull(message = "금액을 입력해 주세요.")
    @Min(value = 0, message = "0보다 큰 수를 입력해주세요.")
    private BigDecimal amount;

    public ExchangeRequestDto (ReservedExchange reservedExchange){
        this.fromBoxId = reservedExchange.getBox().getId();
        this.fromCurrency = reservedExchange.getFromCurrency().getCode();
        this.toCurrency = reservedExchange.getToCurrency().getCode();
        this.amount = reservedExchange.getAmount();
    }
}
