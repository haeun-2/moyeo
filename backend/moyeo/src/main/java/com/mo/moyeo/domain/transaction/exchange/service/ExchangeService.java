package com.mo.moyeo.domain.transaction.exchange.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.common.util.batch.BatchInsert;
import com.mo.moyeo.common.util.finance_api.AccountUtil;
import com.mo.moyeo.domain.box.entity.Box;
import com.mo.moyeo.domain.box.entity.BoxBalance;
import com.mo.moyeo.domain.box.service.BoxBalanceService;
import com.mo.moyeo.domain.box.service.BoxMemberService;
import com.mo.moyeo.domain.box.service.BoxService;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.currency.service.CurrencyService;
import com.mo.moyeo.domain.exchange.rate.dto.CurrentExchangeRateDto;
import com.mo.moyeo.domain.exchange.rate.service.ExchangeRateCacheService;
import com.mo.moyeo.domain.transaction.exchange.dto.ExchangeRequestDto;
import com.mo.moyeo.domain.transaction.exchange.entity.ExchangeTransaction;
import com.mo.moyeo.domain.transaction.exchange.repository.ExchangeRepository;
import com.mo.moyeo.domain.transaction.history.entity.BoxHistory;
import com.mo.moyeo.domain.transaction.transaction.entity.Transaction;
import com.mo.moyeo.domain.transaction.transaction.service.TransactionService;
import com.mo.moyeo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ExchangeService {
    private final ExchangeRepository exchangeRepository;
    private final BoxService boxService;
    private final BoxMemberService boxMemberService;
    private final TransactionService transactionService;
    private final ExchangeRateCacheService exchangeRateCacheService;
    private final CurrencyService currencyService;
    private final AccountUtil accountUtil;
    private final BoxBalanceService boxBalanceService;
    private final BatchInsert batchInsert;

    @Transactional
    public void exchange(User user, ExchangeRequestDto exchangeRequestDto) {
        Box box = boxService.getBoxById(exchangeRequestDto.getFromBoxId());
        validateCondition(user, exchangeRequestDto, box);
        Transaction transaction = transactionService.makeExchangeTransaction(box, user);
        Map<String, CurrentExchangeRateDto> currentExchangeRate = exchangeRateCacheService.getCurrentExchangeRate();

        Double fromAmount, toAmount;

        if (exchangeRequestDto.getFromCurrency() == CurrencyType.KRW) {
            ExchangeTransaction exchangeTransaction = exchangeFromKRW(transaction, currentExchangeRate, exchangeRequestDto);
            exchangeRepository.save(exchangeTransaction);
            fromAmount = exchangeTransaction.getFromAmount();
            toAmount = exchangeTransaction.getToAmount();

        } else if (exchangeRequestDto.getToCurrency() == CurrencyType.KRW) {
            ExchangeTransaction exchangeTransaction = exchangeToKRW(transaction, currentExchangeRate, exchangeRequestDto);
            exchangeRepository.save(exchangeTransaction);
            fromAmount = exchangeTransaction.getFromAmount();
            toAmount = exchangeTransaction.getToAmount();
        } else {
            List<ExchangeTransaction> exchangeTransactions = exchangeThroughKRW(transaction, currentExchangeRate, exchangeRequestDto);
            batchInsert.saveBatch(exchangeTransactions);
            fromAmount = exchangeTransactions.get(0).getFromAmount();
            toAmount = exchangeTransactions.get(1).getToAmount();
        }

        updateBoxBalanceAndSaveHistory(exchangeRequestDto, box, toAmount, fromAmount, transaction);


        //TODO: 수시입출금으로 account 에서 amount 빼고 더해주기
        String toAccount = accountUtil.getAccountByType(exchangeRequestDto.getToCurrency());
        String fromAccount = accountUtil.getAccountByType(exchangeRequestDto.getFromCurrency());
    }

    private void updateBoxBalanceAndSaveHistory(ExchangeRequestDto exchangeRequestDto, Box box, Double toAmount, Double fromAmount, Transaction transaction) {
        BoxBalance fromBoxBalance = boxBalanceService.findBoxBalanceByBoxIdAndCurrencyType(box, exchangeRequestDto.getFromCurrency());
        BoxBalance toBoxBalance = boxBalanceService.findBoxBalanceByBoxIdAndCurrencyType(box, exchangeRequestDto.getToCurrency());

        toBoxBalance.increaseBalance(toAmount);
        if(fromBoxBalance.getBalance() < fromAmount)
            throw new CustomException(ErrorCode.BAD_REQUEST, "환전에 필요한 금액이 부족합니다.");
        fromBoxBalance.decreaseBalance(fromAmount);

        BoxHistory boxHistory1 = BoxHistory.builder()
                .box(box)
                .transaction(transaction)
                .amount(-1.0 * fromAmount)
                .currencyCode(exchangeRequestDto.getFromCurrency())
                .totalAmount(fromBoxBalance.getBalance())
                .title("환전")
                .type(Transaction.Type.EXCHANGE)
                .build();

        BoxHistory boxHistory2 = BoxHistory.builder()
                .box(box)
                .transaction(transaction)
                .amount(toAmount)
                .currencyCode(exchangeRequestDto.getToCurrency())
                .totalAmount(toBoxBalance.getBalance())
                .title("환전")
                .type(Transaction.Type.EXCHANGE)
                .build();

        batchInsert.saveBatch(List.of(boxHistory1, boxHistory2));
    }

    private List<ExchangeTransaction> exchangeThroughKRW(Transaction transaction, Map<String, CurrentExchangeRateDto> currentExchangeRate, ExchangeRequestDto exchangeRequestDto) {
        //목표 -> 한화로 환전
        ExchangeTransaction txn1 = exchangeFromKRW(transaction, currentExchangeRate, exchangeRequestDto);

        Double amountKRW = txn1.getFromAmount();
        ExchangeRequestDto dto = ExchangeRequestDto.builder()
                .fromCurrency(exchangeRequestDto.getFromCurrency())
                .toCurrency(CurrencyType.KRW)
                .amount(amountKRW)
                .build();

        ExchangeTransaction txn2 = exchangeToKRW(transaction, currentExchangeRate, dto);
        return List.of(txn2, txn1);
    }

    private ExchangeTransaction exchangeToKRW(Transaction transaction, Map<String, CurrentExchangeRateDto> currentExchangeRate, ExchangeRequestDto exchangeRequestDto) {
        CurrencyType fromCurrency = exchangeRequestDto.getFromCurrency();
        Double sellRate = currentExchangeRate.get(fromCurrency.name()).getSellRate();

        Double toAmount = exchangeRequestDto.getAmount();
        Double fromAmount;
        if (fromCurrency == CurrencyType.JPY) {
            // JPY는 100엔 기준이므로 나눠줘야 함
            fromAmount = (sellRate / 100.0) * toAmount;
        } else {
            // USD, EUR 같은 경우는 1 단위 기준
            fromAmount = sellRate * toAmount;
        }

        return ExchangeTransaction.builder()
                .transaction(transaction)
                .fromCurrency(currencyService.getReferenceByType(fromCurrency))
                .toCurrency(currencyService.getReferenceByType(CurrencyType.KRW))
                .exchangeRate(sellRate)
                .fromAmount(fromAmount)
                .toAmount(toAmount)
                .build();
    }

    private ExchangeTransaction exchangeFromKRW(Transaction transaction, Map<String, CurrentExchangeRateDto> currentExchangeRate, ExchangeRequestDto exchangeRequestDto) {
        CurrencyType toCurrency = exchangeRequestDto.getToCurrency();
        Double buyRate = currentExchangeRate.get(toCurrency.name()).getBuyRate();

        Double toAmount = exchangeRequestDto.getAmount();
        Double fromAmount;
        if (toCurrency == CurrencyType.JPY) {
            // JPY는 100엔 기준
            fromAmount = (buyRate * 100.0) * toAmount;
        } else {
            // USD, EUR 같은 경우는 1 단위 기준
            fromAmount = buyRate * toAmount;
        }

        return ExchangeTransaction.builder()
                .transaction(transaction)
                .fromCurrency(currencyService.getReferenceByType(CurrencyType.KRW))
                .toCurrency(currencyService.getReferenceByType(toCurrency))
                .exchangeRate(buyRate)
                .fromAmount(fromAmount)
                .toAmount(toAmount)
                .build();
    }

    private void validateCondition(User user, ExchangeRequestDto exchangeRequestDto, Box box) {
        if (box.isPersonal() && !box.getOwnerId().equals(user.getId()))//개인 통장이면 주인인지 체크
            throw new CustomException(ErrorCode.ACCESS_DENIED, "권한이 없습니다.");

        if (!box.isPersonal() && !boxMemberService.getMyPermission(box.getId(), user.getId()).getCanExchange())//모임 통장이면 환전 권한 있는지
            throw new CustomException(ErrorCode.ACCESS_DENIED, "권한이 없습니다.");

        if (exchangeRequestDto.getAmount() % 10 != 0)
            throw new CustomException(ErrorCode.BAD_REQUEST, "10 단위로만 환전 가능합니다.");

        double minExchange = 100.0;
        if (minExchange > exchangeRequestDto.getAmount())
            throw new CustomException(ErrorCode.BAD_REQUEST, "최소 환전금액보다 작게 환전할 수 없습니다. " + minExchange);
    }


}
