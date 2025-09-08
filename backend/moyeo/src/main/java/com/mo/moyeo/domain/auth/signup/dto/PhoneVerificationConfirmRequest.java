package com.mo.moyeo.domain.auth.signup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PhoneVerificationConfirmRequest {

    private String sessionId;
    private String phoneNumber;
    private String verificationCode;
}
