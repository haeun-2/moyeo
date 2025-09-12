package com.mo.moyeo.domain.transaction.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BankTransferDTO {

    private String depositAccountNo;
    private String depositTransactionSummary;
    private Double transactionBalance;
    private String withdrawalAccountNo;
    private String withdrawalTransactionSummary;
    private String userKey;
}
