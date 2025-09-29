package com.mo.moyeo.domain.auth.signup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountVerificationConfirmRequest {

    private String sessionId;
    private String email;
    private String bankAccount;
    private String verificationCode;
}
