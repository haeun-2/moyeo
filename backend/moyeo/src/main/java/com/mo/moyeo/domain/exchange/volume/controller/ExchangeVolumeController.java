package com.mo.moyeo.domain.exchange.volume.controller;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.exchange.volume.dto.ExchangeVolumeDto;
import com.mo.moyeo.domain.exchange.volume.entity.ExchangeVolume;
import com.mo.moyeo.domain.exchange.volume.service.ExchangeVolumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exchanges/volumes")
@Tag(name = "ExchangeVolumeController", description = "환전 거래량 관련 api.")
public class ExchangeVolumeController {
    private final ExchangeVolumeService exchangeVolumeService;

    @GetMapping("/sells")
    @Operation(summary = "단위, 통화별 판매 거래량", description = "통화랑 단위 선택. 단위는 m,h,d중 하나 없으면 m")
    public ResponseEntity<List<ExchangeVolumeDto>> getSellVolumeStatistics(
            @RequestParam(required = false) ExchangeVolume.Unit unit,
            @RequestParam CurrencyType currencyType
    ) {
        return ResponseEntity.ok(exchangeVolumeService.getSellVolumeStatistics(currencyType, unit));
    }

    @GetMapping("/buys")
    @Operation(summary = "단위, 통화별 구매 거래량", description = "통화랑 단위 선택. 단위는 m,h,d중 하나 없으면 m")
    public ResponseEntity<List<ExchangeVolumeDto>> getBuyVolumeStatistics(
            @RequestParam(required = false) ExchangeVolume.Unit unit,
            @RequestParam CurrencyType currencyType
    ) {
        return ResponseEntity.ok(exchangeVolumeService.getBuyVolumeStatistics(currencyType, unit));
    }


}
