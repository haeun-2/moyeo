package com.mo.moyeo.domain.transaction.exchange.controller;

import com.mo.moyeo.domain.auth.security.dto.CustomUserDetails;
import com.mo.moyeo.domain.transaction.exchange.dto.ExchangeRequestDto;
import com.mo.moyeo.domain.transaction.exchange.service.ExchangeService;
import com.mo.moyeo.domain.user.entity.User;
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
public class ExchangeController {
    private final ExchangeService exchangeService;

    @PostMapping
    public void exchange(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid ExchangeRequestDto exchangeRequestDto){
        User user = customUserDetails.getUser();
        exchangeService.exchange(user, exchangeRequestDto);
    }
}
