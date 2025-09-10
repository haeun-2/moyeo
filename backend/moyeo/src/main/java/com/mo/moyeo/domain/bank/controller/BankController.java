package com.mo.moyeo.domain.bank.controller;

import com.mo.moyeo.domain.bank.dto.BankInfoResponse;
import com.mo.moyeo.domain.bank.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/banks")
@Tag(name = "BankController", description = "은행 관련 기능을 제공합니다.")
public class BankController {

    private final BankService bankService;

    @GetMapping
    @Operation(summary = "전체 은행 정보 목록 조회", description = "전체 은행 목록을 조회합니다.")
    public ResponseEntity<List<BankInfoResponse>> getAllBankInfos() {

        List<BankInfoResponse> responses = bankService.getAllBankInfos();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{code}")
    @Operation(summary = "단일 은행 정보 조회", description = "특정 은행 정보를 조회합니다.")
    public ResponseEntity<BankInfoResponse> getBankInfo(@PathVariable String code) {

        BankInfoResponse response = bankService.getBankInfo(code);
        return ResponseEntity.ok(response);
    }
}
