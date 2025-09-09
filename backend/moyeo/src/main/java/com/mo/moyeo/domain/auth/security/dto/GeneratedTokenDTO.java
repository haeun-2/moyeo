package com.mo.moyeo.domain.auth.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class GeneratedTokenDTO {

    private String accessToken;
    private String refreshToken;
}