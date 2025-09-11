package com.mo.moyeo.domain.transaction.exchange.controller;

import com.mo.moyeo.domain.auth.security.dto.CustomUserDetails;
import com.mo.moyeo.domain.transaction.exchange.dto.ExchangeRequestDto;
import com.mo.moyeo.domain.transaction.exchange.service.ExchangeService;
import com.mo.moyeo.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exchange")
@Tag(name = "ExchangeController", description = "환전 관련 기능 제공")
public class ExchangeController {
    private final ExchangeService exchangeService;

    @PostMapping
    @Operation(summary = "환전 신청", description = "환전을 신청합니다.")
    public void exchange(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid ExchangeRequestDto exchangeRequestDto){
        User user = customUserDetails.getUser();
        exchangeService.exchange(user, exchangeRequestDto);
    }
}
