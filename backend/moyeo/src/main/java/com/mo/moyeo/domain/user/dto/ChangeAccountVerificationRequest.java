package com.mo.moyeo.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeAccountVerificationRequest {

    private String bankCode;
    private String bankAccount;
    private String verificationCode;
}
