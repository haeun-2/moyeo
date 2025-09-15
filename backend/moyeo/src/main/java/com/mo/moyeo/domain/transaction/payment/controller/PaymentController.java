package com.mo.moyeo.domain.transaction.payment.controller;

import com.mo.moyeo.domain.auth.security.dto.CustomUserDetails;
import com.mo.moyeo.domain.transaction.payment.dto.PaymentRequestDto;
import com.mo.moyeo.domain.transaction.payment.dto.TokenResponse;
import com.mo.moyeo.domain.transaction.payment.service.PaymentService;
import com.mo.moyeo.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@Tag(name = "PaymentController", description = "결제 관련 기능을 제공합니다.")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/qr-code")
    @Operation(summary = "결제 qr생성", description = "결제 qr 토큰 발급해줌.")
    public ResponseEntity<TokenResponse> getPaymentToken(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam Long boxId){
        User user = customUserDetails.getUser();
        return ResponseEntity.ok(paymentService.getQrCode(user,boxId));
    }

    @PostMapping
    @Operation(summary = "결제 요청", description = "테스트용 입력 JPY, merchantId = 10")
    public ResponseEntity<?> getPaymentToken(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody PaymentRequestDto paymentRequestDto){
        User user = customUserDetails.getUser();
        paymentService.payment(user,paymentRequestDto);
        return ResponseEntity.ok().build();
    }
}
