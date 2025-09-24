package com.mo.moyeo.domain.exchange.rate.controller;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.exchange.rate.dto.CurrentExchangeRateDto;
import com.mo.moyeo.domain.exchange.rate.dto.ExchangeRateHistoryDto;
import com.mo.moyeo.domain.exchange.rate.service.ExchangeRateCacheService;
import com.mo.moyeo.domain.exchange.rate.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exchanges/rates")
@Tag(name = "ExchangeRateController", description = "환율 관련 정보")
public class ExchangeRateController {
    private final ExchangeRateService exchangeRateService;
    private final ExchangeRateCacheService exchangeRateCacheService;

    @GetMapping
    @Operation(summary = "현재 환율 조회", description = "제일 최신의 환율 정보를 조회합니다.")
    public ResponseEntity<Map<String, CurrentExchangeRateDto>> getCurrentExchangeRate() {
        return ResponseEntity.ok(exchangeRateCacheService.getCurrentExchangeRate());
    }

    @GetMapping("/histories")
    @Operation(summary = "환율 단일 조회", description = "unit(1h,1d) 중 하나 입력하면 단위 바뀜 없으면 10분 기준임")
    public ResponseEntity<List<ExchangeRateHistoryDto>> getHistory(
            @RequestParam(required = false) String unit,
            @RequestParam CurrencyType currency) {
        return ResponseEntity.ok(exchangeRateService.getHistory(unit, currency));
    }

}
