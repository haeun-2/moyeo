package com.mo.moyeo.domain.transaction.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BankTransactionResponse {

    private Boolean isSuccess;
    private String errorMessage;
}
