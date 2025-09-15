package com.mo.moyeo.domain.transaction.transfer.dto;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class TransferRequest {

    @NotNull(message = "어떤 박스에서 출금할지 선택해주세요.")
    private Long fromBoxId;

    @NotNull(message = "어떤 박스로 입금할지 선택해주세요.")
    private Long toBoxId;

    @NotNull(message = "이체할 통화를 선택해주세요.")
    private CurrencyType currency;

    @NotNull(message = "금액은 필수 입력값입니다.")
    @Positive(message = "금액은 0보다 커야 합니다.")
    private BigDecimal amount;

}
