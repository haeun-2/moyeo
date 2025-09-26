package com.mo.moyeo.domain.exchange.rate.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.common.util.batch.BatchInsert;
import com.mo.moyeo.common.util.finance_api.ApiType;
import com.mo.moyeo.common.util.finance_api.ApiUtil;
import com.mo.moyeo.domain.currency.entity.Currency;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.currency.repository.CurrencyRepository;
import com.mo.moyeo.domain.exchange.rate.dto.ExchangeRateHistoryDto;
import com.mo.moyeo.domain.exchange.rate.dto.ExchangeRateResponse;
import com.mo.moyeo.domain.exchange.rate.entity.ExchangeRate;
import com.mo.moyeo.domain.exchange.rate.repository.ExchangeRateRepository;
import com.mo.moyeo.domain.exchange.reservation.service.ReservedExchangeCronService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private final ReservedExchangeCronService reservedExchangeCronService;

    // 매일 0시 5분에 실행
    @Scheduled(cron = "0 5 0 * * *")
    @SchedulerLock(name = "deleteOldExchangeRates", lockAtMostFor = "5m", lockAtLeastFor = "1m")
    @Transactional
    public void deleteOldExchangeRates() {
        // 7일 전 날짜 계산
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);

        // 삭제 쿼리 실행
        exchangeRateRepository.deleteByRecordedAtBefore(oneWeekAgo);
    }

    @Scheduled(cron = "0 */10 * * * *") // 초, 분, 시, 일, 월, 요일
    @SchedulerLock(name = "getExchangeRate", lockAtMostFor = "5m", lockAtLeastFor = "1m")
    @Transactional
    public void getExchangeRate() {
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
        reservedExchangeCronService.checkReservation();
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
        List<ExchangeRateHistoryDto> list = new ArrayList<>();
        log.debug("환율 조회 {} {}", currencyType, unit);


        if ("1h".equals(unit)) {
            list = exchangeRateRepository.getHistoryBy1h(currencyType.name()).stream()
                    .map(proj -> new ExchangeRateHistoryDto(
                            proj.getBuyRate(),
                            proj.getSellRate(),
                            proj.getOriginalRate(),
                            proj.getPeriod()
                    ))
                    .toList();
        } else if ("1d".equals(unit)) {
            list = exchangeRateRepository.getHistoryBy1d(currencyType.name()).stream()
                    .map(proj -> new ExchangeRateHistoryDto(
                            proj.getBuyRate(),
                            proj.getSellRate(),
                            proj.getOriginalRate(),
                            proj.getPeriod()
                    ))
                    .toList();
        } else {
            list = exchangeRateRepository.getHistoryBy10m(currencyType.name()).stream()
                    .map(proj -> new ExchangeRateHistoryDto(
                            proj.getBuyRate(),
                            proj.getSellRate(),
                            proj.getOriginalRate(),
                            proj.getPeriod()
                    ))
                    .toList();
        }

        return list;
    }
}
