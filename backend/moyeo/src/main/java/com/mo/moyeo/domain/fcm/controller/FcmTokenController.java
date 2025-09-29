package com.mo.moyeo.domain.fcm.controller;

import com.mo.moyeo.domain.auth.security.dto.CustomUserDetails;
import com.mo.moyeo.domain.fcm.dto.FcmTokenResponse;
import com.mo.moyeo.domain.fcm.dto.FcmTokenUpsertRequest;
import com.mo.moyeo.domain.fcm.service.FcmTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm/tokens")
@Tag(name = "FcmTokenController", description = "FCM 토큰 관리 기능을 제공합니다.")
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;

    @PutMapping
    @Operation(summary = "FCM 디바이스 토큰 등록/갱신", description = "유저의 토큰을 새로 등록하거나 갱신합니다.")
    public ResponseEntity<FcmTokenResponse> registerFcmToken(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody FcmTokenUpsertRequest request) {

        FcmTokenResponse response = fcmTokenService.upsertToken(userDetails.getUser(), request);
        return ResponseEntity.ok(response);
    }
}
