package com.mo.moyeo.domain.exchange.volume.service;

import com.mo.moyeo.common.util.batch.BatchInsert;
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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeVolumeCronService {
    private final ExchangeRepository exchangeRepository;
    private final ExchangeVolumeRepository exchangeVolumeRepository;
    private final CurrencyService currencyService;

    @Scheduled(cron = "0 */10 * * * *") // 초, 분, 시, 일, 월, 요일
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

        saveVolumeHistory(startTime, endTime, ExchangeVolume.Type.m);
    }

    // 1시간 단위
    @Scheduled(cron = "0 0 * * * *") // 매 정시마다 실행
//    @Scheduled(fixedDelay = 1000*10)
    @Transactional
    public void collectVolumeEveryHour() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime startTime = endTime.minusHours(1);

        saveVolumeHistory(startTime, endTime, ExchangeVolume.Type.h);
    }

    // 1일 단위
    @Scheduled(cron = "0 0 0 * * *") // 매 자정마다 실행+
//    @Scheduled(fixedDelay = 1000*10)
    @Transactional
    public void collectVolumeEveryDay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime startTime = endTime.minusDays(1);

        saveVolumeHistory(startTime, endTime, ExchangeVolume.Type.d);
    }

    private void saveVolumeHistory(LocalDateTime startTime, LocalDateTime endTime, ExchangeVolume.Type type) {
        List<ExchangeVolumeGroupDto> transactions =
                exchangeRepository.findVolumeByCurrency(startTime, endTime);

        List<ExchangeVolume> exchangeVolumes = transactions.stream()
                .map(t -> ExchangeVolume.builder()
                        .currency(currencyService.getReferenceByType(CurrencyType.valueOf(t.getCurrencyCode())))
                        .amount(t.getAmount())
                        .type(type)
                        .recordedAt(endTime)
                        .build())
                .toList();

        exchangeVolumeRepository.saveAll(exchangeVolumes);
    }
}
