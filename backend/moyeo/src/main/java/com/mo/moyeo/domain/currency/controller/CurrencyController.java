package com.mo.moyeo.domain.currency.controller;

import com.mo.moyeo.domain.currency.dto.CurrencyListDto;
import com.mo.moyeo.domain.currency.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/currency")
@Tag(name = "CurrencyController", description = "통화 관련 기능 제공")
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping("/list")
    @Operation(summary = "통화 조회", description = "거래 가능한 통화 목록 조회.")
    public ResponseEntity<List<CurrencyListDto>> getCurrencyList() {
        return ResponseEntity.ok(currencyService.getCurrencyList());
    }

}
