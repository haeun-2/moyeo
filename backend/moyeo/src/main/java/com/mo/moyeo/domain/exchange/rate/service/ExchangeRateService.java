package com.mo.moyeo.domain.exchange.rate.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.common.util.batch.BatchInsert;
import com.mo.moyeo.common.util.finance_api.ApiType;
import com.mo.moyeo.common.util.finance_api.ApiUtil;
import com.mo.moyeo.domain.currency.entity.Currency;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.currency.repository.CurrencyRepository;
import com.mo.moyeo.domain.exchange.rate.dto.CurrentExchangeRateDto;
import com.mo.moyeo.domain.exchange.rate.dto.ExchangeRateHistoryDto;
import com.mo.moyeo.domain.exchange.rate.repository.ExchangeRateRepository;
import com.mo.moyeo.domain.exchange.rate.dto.ExchangeRateResponse;
import com.mo.moyeo.domain.exchange.rate.entity.ExchangeRate;
import com.mo.moyeo.domain.exchange.reservation.service.ReservedExchangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final CurrencyRepository currencyRepository;
    private final BatchInsert batchInsert;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Value("${EXCHANGE_RATE_GET}")
    private String exchangeRateUrl;
    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeRateCacheService exchangeRateCacheService;
    private final ReservedExchangeService reservedExchangeService;
    private final RedissonClient redissonClient;

    // 매일 0시 5분에 실행
    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void deleteOldExchangeRates() {
        // 7일 전 날짜 계산
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);

        // 삭제 쿼리 실행
        exchangeRateRepository.deleteByRecordedAtBefore(oneWeekAgo);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    @Transactional
    public void getScheduledLock() {
        RLock lock = redissonClient.getLock("myScheduledJobLock");
        boolean available = false;
        try{
            available = lock.tryLock(0, 10, TimeUnit.SECONDS);
            if (available) {
                doScheduledTask();
            } else {
                log.info("다른 서버에서 실행 중, skip");
            }
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (available) lock.unlock();
        }

    }

    private void doScheduledTask() {
        ExchangeRateResponse exchangeRateResponse = apiCall();
        // recordedAt 설정 (첫 번째 REC 기준)
        LocalDateTime recordedAt = LocalDateTime.parse(
                exchangeRateResponse.getRec().get(0).getCreated(),
                formatter
        );

        // ExchangeRate 리스트 생성
        List<ExchangeRate> exchangeRates = exchangeRateResponse.getRec().stream()
                .map(rec -> {
                    CurrencyType currencyType = CurrencyType.valueOf(rec.getCurrency());
                    Currency currency = currencyRepository.getReferenceById(currencyType);

                    BigDecimal originalRate;
                    try {
                        Number number = NumberFormat.getNumberInstance(Locale.US)
                                .parse(rec.getExchangeRate());
                        originalRate = new BigDecimal(number.toString());
                    } catch (ParseException e) {
                        throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
                    }

                    return ExchangeRate.builder()
                            .currency(currency)
                            .originalRate(originalRate)
                            .buyRate(originalRate.multiply(BigDecimal.valueOf(1.01)))
                            .sellRate(originalRate.multiply(BigDecimal.valueOf(0.99)))
                            .recordedAt(recordedAt)
                            .build();
                })
                .toList();

        // batch 저장
        batchInsert.saveBatch(exchangeRates);
        exchangeRateCacheService.cacheCurrentExchangeRate(exchangeRates);

        //예약환전 체크
        reservedExchangeService.checkReservation();
    }

    private ExchangeRateResponse apiCall() {
        // Header 생성 (ApiUtil 사용)
        Map<String, Object> header = ApiUtil.createHeader(ApiType.exchangeRate.name());

        // body 만들기
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("Header", header);

        // HTTP 요청 헤더 (Content-Type 등)
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, httpHeaders);

        // POST 요청 보내기
        return restTemplate.postForObject(exchangeRateUrl, requestEntity, ExchangeRateResponse.class);
    }

    public List<ExchangeRateHistoryDto> getHistory(String unit, CurrencyType currencyType) {
        String timeFormat;
        if ("10m".equals(unit)) {
            timeFormat = "%Y-%m-%d %H:%i";
        } else if ("1h".equals(unit)) {
            timeFormat = "%Y-%m-%d %H:00:00";
        } else if ("1d".equals(unit)) {
            timeFormat = "%Y-%m-%d";
        } else {
            timeFormat = "%Y-%m-%d %H:%i";
        }

        return exchangeRateRepository.getExchangeRateStatisticsByCurrency(timeFormat, currencyType)
                .stream()
                .map(proj -> new ExchangeRateHistoryDto(
                        proj.getBuyRate(),
                        proj.getSellRate(),
                        proj.getOriginalRate(),
                        proj.getPeriod()
                ))
                .toList();
    }
}
