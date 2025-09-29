package com.mo.moyeo.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountChangeRequest {

    private String bankCode;
    private String bankAccount;
}
