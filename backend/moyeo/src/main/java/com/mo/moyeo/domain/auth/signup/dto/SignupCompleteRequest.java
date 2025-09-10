package com.mo.moyeo.domain.auth.signup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupCompleteRequest {

    private String sessionId;
    private String name;
    private String email;
    private String phoneNumber;
    private String fid;
    private String connectedBankCode;
    private String connectedBankAccount;
}
