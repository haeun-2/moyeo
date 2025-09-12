package com.mo.moyeo.domain.user.controller;

import com.mo.moyeo.domain.auth.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "UserController", description = "유저 관련 기능 제공")
public class UserController {
    private final AuthService authService;

    @PostMapping("/test")
    @Operation(summary = "유저 3번 access token발급", description = "스웨거 테스트용 기능입니다.")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(authService.tempLogin(3L));
    }

    @PostMapping("/test2")
    @Operation(summary = "유저 4번 access token발급", description = "스웨거 테스트용 기능입니다.")
    public ResponseEntity<?> test2() {
        return ResponseEntity.ok(authService.tempLogin(4L));
    }
}