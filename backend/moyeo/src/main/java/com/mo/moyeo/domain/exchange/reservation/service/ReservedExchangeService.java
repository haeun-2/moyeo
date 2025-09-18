package com.mo.moyeo.domain.exchange.reservation.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.box.box.entity.Box;
import com.mo.moyeo.domain.box.balance.entity.BoxBalance;
import com.mo.moyeo.domain.box.balance.service.BoxBalanceService;
import com.mo.moyeo.domain.box.member.service.BoxMemberService;
import com.mo.moyeo.domain.box.box.service.BoxService;
import com.mo.moyeo.domain.currency.entity.Currency;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.currency.service.CurrencyService;
import com.mo.moyeo.domain.exchange.rate.dto.CurrentExchangeRateDto;
import com.mo.moyeo.domain.exchange.rate.service.ExchangeRateCacheService;
import com.mo.moyeo.domain.exchange.reservation.dto.ExchangeReserveDto;
import com.mo.moyeo.domain.exchange.reservation.dto.ExchangeReserveListDto;
import com.mo.moyeo.domain.exchange.reservation.entity.ReservedExchange;
import com.mo.moyeo.domain.exchange.reservation.repository.ReservedExchangeRepository;
import com.mo.moyeo.domain.transaction.category.entity.CategoryType;
import com.mo.moyeo.domain.transaction.category.service.CategoryService;
import com.mo.moyeo.domain.transaction.exchange.dto.ExchangeRequestDto;
import com.mo.moyeo.domain.transaction.exchange.service.ExchangeService;
import com.mo.moyeo.domain.transaction.history.entity.BoxHistory;
import com.mo.moyeo.domain.transaction.history.service.BoxHistoryService;
import com.mo.moyeo.domain.transaction.transaction.entity.Transaction;
import com.mo.moyeo.domain.transaction.transaction.service.TransactionService;
import com.mo.moyeo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReservedExchangeService {
    private final ReservedExchangeRepository reservedExchangeRepository;
    private final BoxService boxService;
    private final CurrencyService currencyService;
    private final BoxBalanceService boxBalanceService;
    private final BoxHistoryService boxHistoryService;
    private final TransactionService transactionService;
    private final BoxMemberService boxMemberService;
    private final ExchangeService exchangeService;
    private final ExchangeRateCacheService exchangeRateCacheService;
    private final CategoryService categoryService;

    @Transactional
    public void reserveExchange(User user, ExchangeReserveDto exchangeReserveDto) {
        Box box = boxService.getBoxById(exchangeReserveDto.boxId());
        Currency fromCurrency = currencyService.getReferenceByType(exchangeReserveDto.fromCurrency());
        Currency toCurrency = currencyService.getReferenceByType(exchangeReserveDto.toCurrency());
        validateCondition(user, exchangeReserveDto, box);

        //예약 환전 저장
        Transaction transaction = transactionService.makeExchangeReservationTransaction(box, user);
        ReservedExchange reservedExchange =
                ReservedExchange.builder()
                        .box(box)
                        .transaction(transaction)
                        .fromCurrency(fromCurrency)
                        .toCurrency(toCurrency)
                        .targetRate(exchangeReserveDto.targetRate())
                        .amount(exchangeReserveDto.amount())
                        .expiresAt(exchangeReserveDto.expiresAt()).build();
        reservedExchangeRepository.save(reservedExchange);

        //예상 금액 차감
        BigDecimal amount = reservedExchange.getTargetRate().multiply(reservedExchange.getAmount());
        BoxBalance fromBoxBalance = boxBalanceService.findBoxBalanceByBoxAndCurrencyType(box, exchangeReserveDto.fromCurrency());
        if(fromBoxBalance.checkSufficientBalance(amount))
            throw new CustomException(ErrorCode.BAD_REQUEST, "환전에 필요한 금액이 부족합니다.");
        fromBoxBalance.decreaseBalance(amount);

        //트랜잭션 및 내역 저장
        BoxHistory boxHistory = BoxHistory.builder()
                .box(box)
                .transaction(transaction)
                .amount(amount)
                .currencyCode(exchangeReserveDto.fromCurrency())
                .totalAmount(fromBoxBalance.getBalance())
                .title("예약 환전")
                .type(Transaction.Type.EXCHANGE_RESERVATION)
                .category(categoryService.getByName(CategoryType.EXCHANGE))
                .createdAt(transaction.getCreatedAt())
                .build();

        boxHistoryService.saveHistory(boxHistory);
    }

    private void validateCondition(User user, ExchangeReserveDto exchangeReserveDto, Box box) {
        if (exchangeReserveDto.fromCurrency() != CurrencyType.KRW && exchangeReserveDto.toCurrency() != CurrencyType.KRW)
            throw new CustomException(ErrorCode.BAD_REQUEST, "외화 간 환전은 예약이 지원되지 않습니다.");

        boxMemberService.validateExchangePermission(box, user);

        BigDecimal amount = exchangeReserveDto.amount();
        BigDecimal ten = BigDecimal.TEN;
        BigDecimal minExchange = BigDecimal.valueOf(100);

        if (amount.remainder(ten).compareTo(BigDecimal.ZERO) != 0) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "10 단위로만 환전 가능합니다.");
        }

        if (amount.compareTo(minExchange) < 0) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "최소 환전금액보다 작게 환전할 수 없습니다. " + minExchange);
        }
    }

    public List<ExchangeReserveListDto> getReservations(User user, Long boxId) {
        //멤버인지 체크하기
        boxMemberService.validateJoinedBoxMember(boxId, user.getId());
        return reservedExchangeRepository.findReservationList(boxId);
    }

    @Transactional
    public void cancelReservation(User user, Long reservationId) {
        ReservedExchange reservedExchange = getReservationById(reservationId);
        boxMemberService.validateJoinedBoxMember(reservedExchange.getBox().getId(), user.getId());

        reservedExchange.cancelReservation();

        // 차감 금액 복원
        BigDecimal amount = reservedExchange.getTargetRate().multiply(reservedExchange.getAmount());
        BoxBalance fromBoxBalance = boxBalanceService.findBoxBalanceByBoxAndCurrencyType(
                reservedExchange.getBox(),
                reservedExchange.getFromCurrency().getCode()
        );
        fromBoxBalance.increaseBalance(amount);

    // 트랜잭션 및 내역 저장
        Transaction transaction = transactionService.makeExchangeReservationTransaction(
                reservedExchange.getBox(), user
        );

        BoxHistory boxHistory = BoxHistory.builder()
                .box(reservedExchange.getBox())
                .transaction(transaction)
                .amount(amount)
                .currencyCode(reservedExchange.getFromCurrency().getCode())
                .totalAmount(fromBoxBalance.getBalance())
                .title("예약 환전 취소")
                .type(Transaction.Type.EXCHANGE_RESERVATION)
                .createdAt(transaction.getCreatedAt())
                .build();

        boxHistoryService.saveHistory(boxHistory);
    }

    public ReservedExchange getReservationById(Long reservationId) {
        return reservedExchangeRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    private List<ReservedExchange> getWaitingReservation() {
        return reservedExchangeRepository.findWaitingReservation();
    }

    @Transactional
    public void checkReservation() {
        Map<String, CurrentExchangeRateDto> currentExchangeRate = exchangeRateCacheService.getCurrentExchangeRate();

        List<ReservedExchange> reservations = getWaitingReservation();
        for (ReservedExchange reservedExchange : reservations) {
            BigDecimal currentRate;
            BigDecimal targetRate = reservedExchange.getTargetRate();

            CurrentExchangeRateDto rateDto = currentExchangeRate.get(reservedExchange.getFromCurrency().getCode().name());

            if (reservedExchange.getFromCurrency().getCode() == CurrencyType.KRW) {
                // 한->외, 사는 경우
                currentRate = rateDto.getBuyRate();
                if (currentRate.compareTo(targetRate) >= 0) { // currentRate >= targetRate
                    completeReservation(reservedExchange);
                }
            } else {
                // 외->한, 파는 경우
                currentRate = rateDto.getSellRate();
                if (currentRate.compareTo(targetRate) <= 0) { // currentRate <= targetRate
                    completeReservation(reservedExchange);
                }
            }
        }
    }


    private void completeReservation(ReservedExchange reservedExchange) {
        //완성 처리
        reservedExchange.completeReservation();

        //차감 금액 복원
        BigDecimal amount = reservedExchange.getTargetRate().multiply(reservedExchange.getAmount());
        BoxBalance fromBoxBalance = boxBalanceService.findBoxBalanceByBoxAndCurrencyType(reservedExchange.getBox(), reservedExchange.getFromCurrency().getCode());
        fromBoxBalance.increaseBalance(amount);

        exchangeService.exchange(reservedExchange.getUser(), new ExchangeRequestDto(reservedExchange));
    }

    // 매일 0시 1분에 실행
    @Scheduled(cron = "0 1 0 * * *")
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
