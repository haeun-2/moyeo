package com.mo.moyeo.domain.transaction.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class BankTransferDTO {

    private String depositAccountNo;
    private String depositTransactionSummary;
    private BigDecimal transactionBalance;
    private String withdrawalAccountNo;
    private String withdrawalTransactionSummary;
    private String userKey;
}
