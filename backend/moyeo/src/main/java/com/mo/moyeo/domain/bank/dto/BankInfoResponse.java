package com.mo.moyeo.domain.bank.dto;

import com.mo.moyeo.domain.bank.entity.Bank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BankInfoResponse {

    private String bankCode;
    private String bankName;
    private String encodedLogoImg;

    public static BankInfoResponse from(Bank bank) {
        return BankInfoResponse.builder()
                .bankCode(bank.getCode())
                .bankName(bank.getBankName())
                .encodedLogoImg(bank.getLogoImg())
                .build();
    }
}
