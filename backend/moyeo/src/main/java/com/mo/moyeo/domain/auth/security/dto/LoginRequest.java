package com.mo.moyeo.domain.auth.security.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {
    private String phoneNumber;
    private String fid;
}