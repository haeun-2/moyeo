package com.mo.moyeo.domain.auth.signup.controller;

import com.mo.moyeo.domain.auth.signup.dto.*;
import com.mo.moyeo.domain.auth.signup.service.SignupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth/signup")
@RequiredArgsConstructor
@Tag(name = "SignupController", description = "회원가입 기능 제공")
public class SignupController {

    private final SignupService signupService;

    @PostMapping("/email")
    @Operation(summary = "이메일 인증 요청", description = "이메일을 검증하고 인증코드 전송을 요청합니다.")
    public ResponseEntity<VerificationResponse> verifyEmail(@RequestBody EmailVerificationRequest request) {
        VerificationResponse response = signupService.verifyEmail(request);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/email/verify-code")
    @Operation(summary = "이메일 인증 코드 검증", description = "이메일 인증 코드가 올바른지 검증합니다.")
    public ResponseEntity<VerificationResponse> verifyEmailCode(@RequestBody EmailVerificationConfirmRequest request) {
        VerificationResponse response = signupService.verifyEmailCode(request);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/phone")
    @Operation(summary = "전화번호 인증 요청", description = "전화번호를 검증하고 인증 문자 전송을 요청합니다.")
    public ResponseEntity<VerificationResponse> verifyPhone(@RequestBody PhoneVerificationRequest request) {
        VerificationResponse response = signupService.verifyPhone(request);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/phone/verify-code")
    @Operation(summary = "전화번호 인증 문자 검증", description = "인증 문자가 올바른지 검증합니다.")
    public ResponseEntity<VerificationResponse> verifySmsCode(@RequestBody PhoneVerificationConfirmRequest request) {
        VerificationResponse response = signupService.verifySmsCode(request);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/account")
    @Operation(summary = "계좌 인증 요청", description = "계좌를 검증하고 1원 인증 송금을 요청합니다.")
    public ResponseEntity<VerificationResponse> verifyAccount(@RequestBody AccountVerificationRequest request) {
        VerificationResponse response = signupService.verifyAccount(request);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/account/verify-code")
    @Operation(summary = "계좌 1원 인증 검증", description = "1원 인증 코드가 올바른지 검증합니다.")
    public ResponseEntity<VerificationResponse> verifyWonCode(@RequestBody AccountVerificationConfirmRequest request) {
        VerificationResponse response = signupService.verifyWonCode(request);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    @Operation(summary = "회원가입 최종 처리 요청", description = "회원가입을 완료합니다.")
    public ResponseEntity<Void> completeSignup(@RequestBody SignupCompleteRequest request) {
        signupService.completeSignup(request);
        return ResponseEntity.ok().build();
    }
}
