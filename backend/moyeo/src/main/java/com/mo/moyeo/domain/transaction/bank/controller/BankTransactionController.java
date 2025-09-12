package com.mo.moyeo.domain.transaction.bank.controller;

import com.mo.moyeo.domain.auth.security.dto.CustomUserDetails;
import com.mo.moyeo.domain.transaction.bank.dto.*;
import com.mo.moyeo.domain.transaction.bank.service.BankTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/banks")
@RequiredArgsConstructor
@Tag(name = "BankTransactionController", description = "연결 계좌 거래 기능 제공")
public class BankTransactionController {

    private final BankTransactionService bankTransactionService;

    @PostMapping("/deposit")
    @Operation(summary = "모여머니 충전", description = "연결계좌에서 출금하여 모여머니를 충전합니다.")
    public ResponseEntity<DepositResponse> deposit(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody DepositRequest request) {

        DepositResponse response = bankTransactionService.charge(userDetails.getUser(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdrawal")
    @Operation(summary = "모여머니 현금화", description = "박스에서 출금하여 연결계좌로 입금합니다.")
    public ResponseEntity<WithdrawResponse> deposit(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody WithdrawRequest request) {

        WithdrawResponse response = bankTransactionService.discharge(userDetails.getUser(), request);
        return ResponseEntity.ok(response);
    }
}
