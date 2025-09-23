package com.mo.moyeo.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AccountInfoResponse {

    private String bankName;
    private String bankLogoImg;
    private String bankAccount;
}
