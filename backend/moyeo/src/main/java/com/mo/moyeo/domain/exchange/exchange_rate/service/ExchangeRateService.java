package com.mo.moyeo.domain.exchange.exchange_rate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    @Scheduled(fixedDelay = 1000*60*10)
    public void getExchangeRate(){

    }
}
