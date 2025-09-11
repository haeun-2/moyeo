package com.mo.moyeo.domain.exchange_rate.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.common.util.batch.BatchInsert;
import com.mo.moyeo.common.util.finance_api.ApiType;
import com.mo.moyeo.common.util.finance_api.ApiUtil;
import com.mo.moyeo.domain.currency.entity.Currency;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.currency.repository.CurrencyRepository;
import com.mo.moyeo.domain.exchange_rate.dto.CurrentExchangeRateDto;
import com.mo.moyeo.domain.exchange_rate.dto.ExchangeRateHistoryDto;
import com.mo.moyeo.domain.exchange_rate.dto.ExchangeRateResponse;
import com.mo.moyeo.domain.exchange_rate.entity.ExchangeRate;
import com.mo.moyeo.domain.exchange_rate.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final CurrencyRepository currencyRepository;
    private final BatchInsert batchInsert;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Value("${EXCHANGE_RATE_GET}")
    private String exchangeRateUrl;
    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeRateCacheService exchangeRateCacheService;

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
    public void getExchangeRate() {
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
        ExchangeRateResponse exchangeRateResponse = restTemplate.postForObject(exchangeRateUrl, requestEntity, ExchangeRateResponse.class);

        // recordedAt 설정 (첫 번째 REC 기준)
        LocalDateTime recordedAt = LocalDateTime.parse(
                exchangeRateResponse.getRec().get(0).getCreated(),
                formatter
        );

        //현재 환율 정보 캐싱
        List<CurrentExchangeRateDto> currentExchangeRate = new ArrayList<>();

        // ExchangeRate 리스트 생성
        List<ExchangeRate> exchangeRates = exchangeRateResponse.getRec().stream()
                .map(rec -> {
                    CurrencyType currencyType = CurrencyType.valueOf(rec.getCurrency());
                    Currency currency = currencyRepository.getReferenceById(currencyType);

                    double originalRate, exchangeMin;
                    try {
                        originalRate = NumberFormat.getNumberInstance(Locale.US)
                                .parse(rec.getExchangeRate())
                                .doubleValue();
                        exchangeMin = NumberFormat.getNumberInstance(Locale.US)
                                .parse(rec.getExchangeMin())
                                .doubleValue();
                    } catch (ParseException e) {
                        throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
                    }

                    ExchangeRate exchangeRate = ExchangeRate.builder()
                            .currency(currency)
                            .originalRate(originalRate)
                            .buyRate(originalRate * 1.01)
                            .sellRate(originalRate * 0.99)
                            .exchangeMin(exchangeMin)
                            .recordedAt(recordedAt)
                            .build();

                    currentExchangeRate.add(new CurrentExchangeRateDto(exchangeRate));
                    return exchangeRate;
                })
                .toList();

        // batch 저장
        batchInsert.saveBatch(exchangeRates);
        exchangeRateCacheService.cacheCurrentExchangeRate(exchangeRates);
    }

    public List<ExchangeRateHistoryDto> getHistory(CurrencyType currencyType) {
        Currency currency = currencyRepository.getReferenceById(currencyType);
        return exchangeRateRepository.getExchangeRateByCurrency(currency);
    }
}
