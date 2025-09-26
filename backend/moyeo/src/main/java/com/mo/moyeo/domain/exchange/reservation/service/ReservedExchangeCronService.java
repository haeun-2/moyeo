package com.mo.moyeo.domain.exchange.reservation.service;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.exchange.rate.dto.CurrentExchangeRateDto;
import com.mo.moyeo.domain.exchange.rate.service.ExchangeRateCacheService;
import com.mo.moyeo.domain.exchange.reservation.entity.ReservedExchange;
import com.mo.moyeo.domain.exchange.reservation.repository.ReservedExchangeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservedExchangeCronService {
    private final ExchangeRateCacheService exchangeRateCacheService;
    private final ReservedExchangeRepository reservedExchangeRepository;
    private final ReservedExchangeService reservedExchangeService;

    @Scheduled(initialDelay = 1000*10, fixedDelay = 1000*60*10)
    @SchedulerLock(name = "checkReservation", lockAtMostFor = "5m", lockAtLeastFor = "1m")
    @Transactional
    public void checkReservation() {
        Map<String, CurrentExchangeRateDto> currentExchangeRate = exchangeRateCacheService.getCurrentExchangeRate();
        log.debug("예약 환전 체크");

        List<ReservedExchange> reservations = getWaitingReservation();
        for (ReservedExchange reservedExchange : reservations) {
            BigDecimal currentRate;
            BigDecimal targetRate = reservedExchange.getTargetRate();

            CurrencyType currencyType;
            if(reservedExchange.getFromCurrency().getCode()==CurrencyType.KRW) {
                currencyType = reservedExchange.getToCurrency().getCode();
            } else {
                currencyType = reservedExchange.getFromCurrency().getCode();
            }

            CurrentExchangeRateDto rateDto = currentExchangeRate.get(currencyType.name());

            if (reservedExchange.getFromCurrency().getCode() == CurrencyType.KRW) {
                // 한->외, 사는 경우
                currentRate = rateDto.getBuyRate();
                if (currentRate.compareTo(targetRate) <= 0) { // currentRate >= targetRate
                    reservedExchangeService.completeReservation(reservedExchange);
                }
            } else {
                // 외->한, 파는 경우
                currentRate = rateDto.getSellRate();
                if (currentRate.compareTo(targetRate) >= 0) { // currentRate <= targetRate
                    reservedExchangeService.completeReservation(reservedExchange);
                }
            }
            log.debug("cur = {}, target = {}", currentRate, targetRate);
        }
    }

    private List<ReservedExchange> getWaitingReservation() {
        return reservedExchangeRepository.findWaitingReservation();
    }

    // 매일 0시 1분에 실행
    @Scheduled(cron = "0 1 0 * * *")
    @SchedulerLock(name = "deleteOldExchangeRates", lockAtMostFor = "5m", lockAtLeastFor = "1m")
    @Transactional
    public void deleteOldExchangeRates() {
        LocalDate now = LocalDate.now();
        List<ReservedExchange> reservations = getWaitingReservation();
        for (ReservedExchange reservedExchange : reservations) {
            if(now.isAfter(reservedExchange.getExpiresAt())){
                reservedExchange.expire();
            }
        }
    }
}
