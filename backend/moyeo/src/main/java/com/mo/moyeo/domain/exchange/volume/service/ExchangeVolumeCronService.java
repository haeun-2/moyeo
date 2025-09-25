package com.mo.moyeo.domain.exchange.volume.service;

import com.mo.moyeo.common.util.batch.BatchInsert;
import com.mo.moyeo.domain.currency.dto.CurrencyListDto;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.currency.service.CurrencyService;
import com.mo.moyeo.domain.exchange.volume.dto.ExchangeVolumeGroupDto;
import com.mo.moyeo.domain.exchange.volume.entity.ExchangeVolume;
import com.mo.moyeo.domain.exchange.volume.repository.ExchangeVolumeRepository;
import com.mo.moyeo.domain.transaction.exchange.repository.ExchangeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeVolumeCronService {
    private final ExchangeRepository exchangeRepository;
    private final ExchangeVolumeRepository exchangeVolumeRepository;
    private final CurrencyService currencyService;

    @Scheduled(cron = "0 */10 * * * *") // 초, 분, 시, 일, 월, 요일
//    @Scheduled(fixedDelay = 1000*60*10)
    @Transactional
    public void collectRecentVolume() {
        //현재 시간을 yyyy-mm-dd hh:mm으로 가져옴
        //이전 시간을 10분전으로 설정
        LocalDateTime now = LocalDateTime.now();

        // 초 버리고 10분 단위로 내림
        int minute = now.getMinute() / 10 * 10; // 10분 단위로 내림
        LocalDateTime endTime = now.withMinute(minute).withSecond(0).withNano(0);
        // 10분 전 시간
        LocalDateTime startTime = endTime.minusMinutes(10);

        saveVolumeHistory(startTime, endTime, ExchangeVolume.Unit.m);
    }

    // 1시간 단위
    @Scheduled(cron = "0 0 * * * *") // 매 정시마다 실행
//    @Scheduled(fixedDelay = 1000*10)
    @Transactional
    public void collectVolumeEveryHour() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime startTime = endTime.minusHours(1);

        saveVolumeHistory(startTime, endTime, ExchangeVolume.Unit.h);
    }

    // 1일 단위
    @Scheduled(cron = "0 0 0 * * *") // 매 자정마다 실행+
//    @Scheduled(fixedDelay = 1000*10)
    @Transactional
    public void collectVolumeEveryDay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime startTime = endTime.minusDays(1);

        saveVolumeHistory(startTime, endTime, ExchangeVolume.Unit.d);
    }

    private void saveVolumeHistory(LocalDateTime startTime, LocalDateTime endTime, ExchangeVolume.Unit unit) {
        List<CurrencyType> currencyList = currencyService.getCurrencyList().stream().map(CurrencyListDto::getCode).toList();

        List<ExchangeVolume> exchangeBuyVolumes = currencyList.stream() // String 코드
                .map(code -> {
                    BigDecimal amount =
                            exchangeRepository.findBuyVolumeByCurrencyAndTime(startTime, endTime, code);

                    return ExchangeVolume.builder()
                            .currency(currencyService.getReferenceByType(code)) // code 기반
                            .amount(amount)
                            .unit(unit)
                            .type(ExchangeVolume.Type.buy)
                            .recordedAt(endTime)
                            .build();
                })
                .toList();

        List<ExchangeVolume> exchangeSellVolumes = currencyList.stream() // String 코드
                .map(code -> {
                    BigDecimal amount =
                            exchangeRepository.findSellVolumeByCurrencyAndTime(startTime, endTime, code);

                    return ExchangeVolume.builder()
                            .currency(currencyService.getReferenceByType(code)) // code 기반
                            .amount(amount)
                            .unit(unit)
                            .type(ExchangeVolume.Type.sell)
                            .recordedAt(endTime)
                            .build();
                })
                .toList();

        exchangeVolumeRepository.saveAll(exchangeBuyVolumes);
        exchangeVolumeRepository.saveAll(exchangeSellVolumes);
    }
}
