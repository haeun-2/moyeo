package com.mo.moyeo.domain.exchange.rate.service;

import com.mo.moyeo.domain.exchange.rate.dto.CurrentExchangeRateDto;
import com.mo.moyeo.domain.exchange.rate.entity.ExchangeRate;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExchangeRateCacheService {
    private final String CURRENT_EXCHANGE_RATE_KEY = "current_exchange_rate";

    // ====================================
    // Cache Annotation으로 Redis에 저장
    // ====================================
    @CachePut(value = CURRENT_EXCHANGE_RATE_KEY, key = "'all'")
    public Map<String, CurrentExchangeRateDto> cacheCurrentExchangeRate(List<ExchangeRate> exchangeRates) {
        return exchangeRates.stream()
                .collect(Collectors.toMap(
                        er -> er.getCurrency().getCode().name(),
                        CurrentExchangeRateDto::new
                ));
    }

    @Cacheable(value = CURRENT_EXCHANGE_RATE_KEY, key = "'all'")
    public Map<String, CurrentExchangeRateDto> getCurrentExchangeRate() {
        // 캐시에 값이 없으면 빈 Map 반환 (또는 DB 조회 후 반환 가능)
        return new HashMap<>();
    }

}
