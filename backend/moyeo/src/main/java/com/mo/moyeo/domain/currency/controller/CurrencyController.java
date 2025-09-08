package com.mo.moyeo.domain.currency.controller;

import com.mo.moyeo.domain.currency.dto.CurrencyListDto;
import com.mo.moyeo.domain.currency.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/currency")
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping("/list")
    public ResponseEntity<List<CurrencyListDto>> getCurrencyList() {
        return ResponseEntity.ok(currencyService.getCurrencyList());
    }

}
