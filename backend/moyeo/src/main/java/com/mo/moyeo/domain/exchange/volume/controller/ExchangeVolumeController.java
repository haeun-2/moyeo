package com.mo.moyeo.domain.exchange.volume.controller;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.exchange.volume.dto.ExchangeVolumeDto;
import com.mo.moyeo.domain.exchange.volume.service.ExchangeVolumeService;
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
public class ExchangeVolumeController {
    private final ExchangeVolumeService exchangeVolumeService;

    @GetMapping
    public ResponseEntity<List<ExchangeVolumeDto>> getVolumeStatistics(
            @RequestParam(required = false) String unit,
            @RequestParam CurrencyType currencyType
    ) {
        return ResponseEntity.ok(exchangeVolumeService.getVolumeStatistics(currencyType, unit));
    }

}
