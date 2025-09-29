package com.mo.moyeo.domain.auth.security.controller;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.auth.security.config.JwtProperties;
import com.mo.moyeo.domain.auth.security.dto.*;
import com.mo.moyeo.domain.auth.security.service.AuthService;
import com.mo.moyeo.domain.auth.security.service.JwtRedisService;
import com.mo.moyeo.domain.auth.security.util.JwtUtil;
import com.mo.moyeo.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "AuthController", description = "로그인/로그아웃 및 토큰 관리 기능 제공")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final JwtRedisService jwtRedisService;
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "로그인 처리", description = "로그인 기능입니다.")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 처리", description = "로그아웃 기능입니다.")
    public ResponseEntity<?> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {

        authService.logout(userDetails.getUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    @Operation(summary = "액세스 토큰 재발급", description = "Request Body에 담긴 리프레시 토큰을 확인하여, 유효하면 새로운 액세스 토큰을 Response Body에 담아 재발급합니다.")
    public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshRequest request) {

        RefreshResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }
}
