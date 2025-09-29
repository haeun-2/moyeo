package com.mo.moyeo.domain.fcm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FcmTokenUpsertRequest {

    @NotBlank(message = "디바이스 토큰은 필수입니다.")
    private String deviceToken;
}

