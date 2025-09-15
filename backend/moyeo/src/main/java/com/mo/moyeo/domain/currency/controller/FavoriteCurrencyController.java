package com.mo.moyeo.domain.currency.controller;

import com.mo.moyeo.domain.auth.security.dto.CustomUserDetails;
import com.mo.moyeo.domain.currency.dto.FavoriteCurrencyRequest;
import com.mo.moyeo.domain.currency.entity.FavoriteCurrency;
import com.mo.moyeo.domain.currency.service.FavoriteCurrencyService;
import com.mo.moyeo.domain.exchange.rate.dto.CurrentExchangeRateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/currencies/favorite")
@Tag(name = "FavoriteCurrencyController", description = "통화 좋아요 관련 기능 제공")
public class FavoriteCurrencyController {

    private final FavoriteCurrencyService favoriteCurrencyService;

    @PostMapping
    @Operation(summary = "통화 좋아요", description = "관심 통화를 설정합니다.")
    public ResponseEntity<Void> likeCurrency(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid FavoriteCurrencyRequest request) {

        favoriteCurrencyService.likeCurrency(userDetails.getUser(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @Operation(summary = "통화 좋아요 취소", description = "관심 통화를 해제합니다.")
    public ResponseEntity<Void> unlikeCurrency(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid FavoriteCurrencyRequest request) {

        favoriteCurrencyService.unlikeCurrency(userDetails.getUser(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "관심 통화 정보 조회", description = "관심 통화 정보를 조회합니다.")
    public ResponseEntity<List<CurrentExchangeRateDto>> getFavoriteCurrencies(@AuthenticationPrincipal CustomUserDetails userDetails) {

        List<CurrentExchangeRateDto> response = favoriteCurrencyService.getAllFavoriteCurrencies(userDetails.getUser());
        return ResponseEntity.ok(response);
    }
}
