package com.mo.moyeo.domain.transaction.transfer.dto;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransferRequest {

    @NotEmpty(message = "어떤 박스에서 출금할지 선택해주세요.")
    private Long fromBoxId;

    @NotEmpty(message = "어떤 박스로 입금할지 선택해주세요.")
    private Long toBoxId;

    @NotEmpty(message = "이체할 통화를 선택해주세요.")
    private CurrencyType currency;
    
    @NotEmpty(message = "금액을 입력해 주세요.")
    @Min(value = 0, message = "0보다 큰 수를 입력해주세요.")
    private Double amount;

}
