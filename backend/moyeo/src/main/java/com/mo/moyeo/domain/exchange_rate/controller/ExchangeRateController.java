package com.mo.moyeo.domain.exchange_rate.controller;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.exchange_rate.dto.CurrentExchangeRateDto;
import com.mo.moyeo.domain.exchange_rate.dto.ExchangeRateHistoryDto;
import com.mo.moyeo.domain.exchange_rate.service.ExchangeRateCacheService;
import com.mo.moyeo.domain.exchange_rate.service.ExchangeRateService;
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
@RequestMapping("/api/exchange/rates")
public class ExchangeRateController {
    private final ExchangeRateService exchangeRateService;
    private final ExchangeRateCacheService exchangeRateCacheService;

    @GetMapping
    public ResponseEntity<Map<String, CurrentExchangeRateDto>> getCurrentExchangeRate() {
        return ResponseEntity.ok(exchangeRateCacheService.getCurrentExchangeRate());
    }

    @GetMapping("/history")
    public ResponseEntity<List<ExchangeRateHistoryDto>> getHistory(@RequestParam CurrencyType currency) {
        return ResponseEntity.ok(exchangeRateService.getHistory(currency));
    }

}
