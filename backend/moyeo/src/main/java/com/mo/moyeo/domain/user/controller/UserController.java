package com.mo.moyeo.domain.user.controller;

import com.mo.moyeo.domain.auth.security.dto.CustomUserDetails;
import com.mo.moyeo.domain.auth.security.service.AuthService;
import com.mo.moyeo.domain.user.dto.AccountChangeRequest;
import com.mo.moyeo.domain.user.dto.AccountInfoResponse;
import com.mo.moyeo.domain.user.dto.ChangeAccountVerificationRequest;
import com.mo.moyeo.domain.user.dto.UserInfoResponse;
import com.mo.moyeo.domain.user.service.UserInformationService;
import com.mo.moyeo.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "UserController", description = "유저 관련 기능 제공")
public class UserController {
    private final AuthService authService;
    private final UserService userService;
    private final UserInformationService userInformationService;

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

    @GetMapping("/me")
    @Operation(summary = "유저 기본 정보 조회", description = "유저의 기본 정보를 조회합니다.")
    public ResponseEntity<UserInfoResponse> getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {

        UserInfoResponse response = userInformationService.getUserInfo(userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/accounts")
    @Operation(summary = "연결 계좌 정보 조회", description = "유저의 연결된 계좌 상세 정보를 조회합니다.")
    public ResponseEntity<AccountInfoResponse> verifyWonCodeAndChangeAccount(@AuthenticationPrincipal CustomUserDetails userDetails) {

        AccountInfoResponse response = userInformationService.getAccountInfo(userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/accounts/verification")
    @Operation(summary = "1원 인증 코드 요청", description = "연결 계좌 변경을 위한 1원 인증 코드 전송을 요청합니다.")
    public ResponseEntity<Void> requestWonCode(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody AccountChangeRequest request) {

        userService.sendWonCodeForChange(userDetails.getUser(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accounts")
    @Operation(summary = "1원 인증 코드 검증 및 계좌 변경", description = "1원 인증 코드를 검증하고 연결 계좌를 변경합니다.")
    public ResponseEntity<?> verifyWonCodeAndChangeAccount(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ChangeAccountVerificationRequest request) {

        userService.verifyWonCodeForChange(userDetails.getUser(), request);
        return ResponseEntity.ok().build();
    }
}