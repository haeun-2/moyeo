package com.mo.moyeo.domain.exchange.reservation.service;

import com.mo.moyeo.common.annotation.BoxDistributedLock;
import com.mo.moyeo.common.annotation.BoxLockParam;
import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.common.util.lock.LockManager;
import com.mo.moyeo.domain.box.balance.entity.BoxBalance;
import com.mo.moyeo.domain.box.balance.service.BoxBalanceService;
import com.mo.moyeo.domain.box.box.entity.Box;
import com.mo.moyeo.domain.box.box.service.BoxService;
import com.mo.moyeo.domain.box.member.service.BoxMemberService;
import com.mo.moyeo.domain.currency.entity.Currency;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.currency.service.CurrencyService;
import com.mo.moyeo.domain.exchange.rate.service.ExchangeRateCacheService;
import com.mo.moyeo.domain.exchange.reservation.dto.ExchangeReserveDto;
import com.mo.moyeo.domain.exchange.reservation.dto.ExchangeReserveListDto;
import com.mo.moyeo.domain.exchange.reservation.entity.ReservedExchange;
import com.mo.moyeo.domain.exchange.reservation.repository.ReservedExchangeRepository;
import com.mo.moyeo.domain.transaction.category.entity.CategoryType;
import com.mo.moyeo.domain.transaction.category.service.CategoryCacheService;
import com.mo.moyeo.domain.transaction.exchange.dto.ExchangeRequestDto;
import com.mo.moyeo.domain.transaction.exchange.service.ExchangeService;
import com.mo.moyeo.domain.transaction.history.entity.BoxHistory;
import com.mo.moyeo.domain.transaction.history.service.BoxHistoryService;
import com.mo.moyeo.domain.transaction.transaction.entity.Transaction;
import com.mo.moyeo.domain.transaction.transaction.service.TransactionService;
import com.mo.moyeo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
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
    private final CategoryCacheService categoryCacheService;
    private final LockManager lockManager;

    @BoxDistributedLock({
            @BoxLockParam(boxId = "#exchangeReserveDto.boxId", currencyCode = "#exchangeReserveDto.fromCurrency"),
            @BoxLockParam(boxId = "#exchangeReserveDto.boxId", currencyCode = "#exchangeReserveDto.toCurrency")
    })
    @Transactional
    public void reserveExchange(User user, ExchangeReserveDto exchangeReserveDto) {
        Box box = boxService.getBoxById(exchangeReserveDto.boxId());
        Currency fromCurrency = currencyService.getReferenceByType(exchangeReserveDto.fromCurrency());
        Currency toCurrency = currencyService.getReferenceByType(exchangeReserveDto.toCurrency());
        validateCondition(user, exchangeReserveDto, box);
        BigDecimal targetRate = exchangeReserveDto.targetRate();

        //예약 환전 저장
        Transaction transaction = transactionService.makeExchangeReservationTransaction(box, user);
        ReservedExchange reservedExchange =
                ReservedExchange.builder()
                        .box(box)
                        .user(user)
                        .transaction(transaction)
                        .fromCurrency(fromCurrency)
                        .toCurrency(toCurrency)
                        .targetRate(targetRate)
                        .amount(exchangeReserveDto.amount())
                        .expiresAt(exchangeReserveDto.expiresAt()).build();
        reservedExchangeRepository.save(reservedExchange);

        //예상 금액 차감
        BigDecimal fromAmount;
        if (exchangeReserveDto.toCurrency() == CurrencyType.JPY) {
            // JPY는 100엔 기준
            fromAmount = targetRate
                    .multiply(exchangeReserveDto.amount())
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);// div 100;
        }else if(exchangeReserveDto.fromCurrency() == CurrencyType.JPY){
            fromAmount = exchangeReserveDto.amount()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(targetRate, 0, RoundingMode.HALF_UP);
        }else {
            // USD, EUR 같은 경우는 1 단위 기준
            fromAmount = targetRate.multiply(exchangeReserveDto.amount());
        }

        BoxBalance fromBoxBalance = boxBalanceService.findBoxBalanceByBoxAndCurrencyType(box, exchangeReserveDto.fromCurrency());
        if (fromBoxBalance.checkSufficientBalance(fromAmount))
            throw new CustomException(ErrorCode.BAD_REQUEST, "환전에 필요한 금액이 부족합니다.");
        fromBoxBalance.decreaseBalance(fromAmount);

        //트랜잭션 및 내역 저장
        BoxHistory boxHistory = BoxHistory.builder()
                .box(box)
                .transaction(transaction)
                .amount(fromAmount.negate())
                .currencyCode(exchangeReserveDto.fromCurrency())
                .totalAmount(fromBoxBalance.getBalance())
                .title("예약 환전")
                .type(Transaction.Type.EXCHANGE_RESERVATION)
                .category(categoryCacheService.getByName(CategoryType.EXCHANGE))
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
        Box box = boxService.getBoxById(boxId);
        boxMemberService.validateJoinedBoxMember(box, user);
        return reservedExchangeRepository.findReservationList(boxId);
    }

    @Transactional
    public void cancelReservation(User user, Long reservationId) {
        ReservedExchange reservedExchange = getReservationById(reservationId);

        Long boxId = reservedExchange.getBox().getId();
        CurrencyType currencyCode = reservedExchange.getFromCurrency().getCode();

        lockManager.executeWithLock(
                new String[]{"box:" + boxId + ":" + currencyCode},
                () -> {
                    processCancelReservation(reservedExchange, user);
                    return null;
                }
        );
        reservedExchange.cancelReservation();
    }

    public void processCancelReservation(ReservedExchange reservedExchange, User user) {
        boxMemberService.validateJoinedBoxMember(reservedExchange.getBox(), user);

        // 차감 금액 복원
        BigDecimal amount;
        if (reservedExchange.getToCurrency().getCode() == CurrencyType.JPY) {
            // JPY는 100엔 기준

            log.debug("{} {}" ,  reservedExchange.getTargetRate(), reservedExchange.getAmount());
            amount = reservedExchange.getTargetRate()
                    .multiply(reservedExchange.getAmount())
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);// div 100;

        }else if(reservedExchange.getFromCurrency().getCode() == CurrencyType.JPY){
            log.debug("{} {}" ,  reservedExchange.getTargetRate(), reservedExchange.getAmount());

            amount = reservedExchange.getAmount()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(reservedExchange.getTargetRate(), 0, RoundingMode.HALF_UP);
        }else {
            // USD, EUR 같은 경우는 1 단위 기준
            amount = reservedExchange.getTargetRate().multiply(reservedExchange.getAmount());
        }


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
                .category(categoryCacheService.getByName(CategoryType.EXCHANGE))
                .createdAt(transaction.getCreatedAt())
                .build();

        boxHistoryService.saveHistory(boxHistory);
    }

    public ReservedExchange getReservationById(Long reservationId) {
        return reservedExchangeRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @BoxDistributedLock({
            @BoxLockParam(boxId = "#reservedExchange.box.id", currencyCode = "reservedExchange.fromCurrency.code")
    })
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completeReservation(ReservedExchange reservedExchange) {
        //완성 처리
        reservedExchange.completeReservation();

        //예약 환전 취소처리
        processCancelReservation(reservedExchange, reservedExchange.getUser());
        log.debug("예약 환전 취소");

        exchangeService.exchange(reservedExchange.getUser(), new ExchangeRequestDto(reservedExchange));
        reservedExchangeRepository.save(reservedExchange);
    }



    public ReservedExchange getReservationByTxn(Transaction transaction) {
        return reservedExchangeRepository.findByTransaction(transaction)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
