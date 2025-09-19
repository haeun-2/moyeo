package com.mo.moyeo.domain.transaction.exchange.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.common.util.batch.BatchInsert;
import com.mo.moyeo.common.util.finance_api.AccountUtil;
import com.mo.moyeo.domain.box.box.entity.Box;
import com.mo.moyeo.domain.box.balance.entity.BoxBalance;
import com.mo.moyeo.domain.box.balance.service.BoxBalanceService;
import com.mo.moyeo.domain.box.member.service.BoxMemberService;
import com.mo.moyeo.domain.box.box.service.BoxService;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.currency.service.CurrencyService;
import com.mo.moyeo.domain.exchange.rate.dto.CurrentExchangeRateDto;
import com.mo.moyeo.domain.exchange.rate.service.ExchangeRateCacheService;
import com.mo.moyeo.domain.transaction.category.entity.CategoryType;
import com.mo.moyeo.domain.transaction.category.service.CategoryCacheService;
import com.mo.moyeo.domain.transaction.exchange.dto.ExchangeRequestDto;
import com.mo.moyeo.domain.transaction.exchange.entity.ExchangeTransaction;
import com.mo.moyeo.domain.transaction.exchange.repository.ExchangeRepository;
import com.mo.moyeo.domain.transaction.history.entity.BoxHistory;
import com.mo.moyeo.domain.transaction.transaction.entity.Transaction;
import com.mo.moyeo.domain.transaction.transaction.service.TransactionService;
import com.mo.moyeo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
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
    private final CategoryCacheService categoryCacheService;

    @Transactional
    public void exchange(User user, ExchangeRequestDto exchangeRequestDto) {
        Box box = boxService.getBoxById(exchangeRequestDto.getFromBoxId());
        validateCondition(user, exchangeRequestDto, box);
        Transaction transaction = transactionService.makeExchangeTransaction(box, user);
        Map<String, CurrentExchangeRateDto> currentExchangeRate = exchangeRateCacheService.getCurrentExchangeRate();

        BigDecimal fromAmount, toAmount;

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

        log.debug("환전 {} -> {}", fromAmount, toAmount);
        updateBoxBalanceAndSaveHistory(exchangeRequestDto, box, toAmount, fromAmount, transaction);


        //TODO: 수시입출금으로 account 에서 amount 빼고 더해주기
        String toAccount = accountUtil.getAccountByType(exchangeRequestDto.getToCurrency());
        String fromAccount = accountUtil.getAccountByType(exchangeRequestDto.getFromCurrency());
    }

    private void updateBoxBalanceAndSaveHistory(ExchangeRequestDto exchangeRequestDto, Box box, BigDecimal toAmount, BigDecimal fromAmount, Transaction transaction) {
        BoxBalance fromBoxBalance = boxBalanceService.findBoxBalanceByBoxAndCurrencyType(box, exchangeRequestDto.getFromCurrency());
        BoxBalance toBoxBalance = boxBalanceService.findBoxBalanceByBoxAndCurrencyType(box, exchangeRequestDto.getToCurrency());

        toBoxBalance.increaseBalance(toAmount);
        if(fromBoxBalance.checkSufficientBalance(fromAmount))
            throw new CustomException(ErrorCode.BAD_REQUEST, "환전에 필요한 금액이 부족합니다.");
        fromBoxBalance.decreaseBalance(fromAmount);

        BoxHistory boxHistory1 = BoxHistory.builder()
                .box(box)
                .transaction(transaction)
                .amount(fromAmount.negate())
                .currencyCode(exchangeRequestDto.getFromCurrency())
                .totalAmount(fromBoxBalance.getBalance())
                .title("환전")
                .type(Transaction.Type.EXCHANGE)
                .category(categoryCacheService.getByName(CategoryType.EXCHANGE))
                .createdAt(transaction.getCreatedAt())
                .build();

        BoxHistory boxHistory2 = BoxHistory.builder()
                .box(box)
                .transaction(transaction)
                .amount(toAmount)
                .currencyCode(exchangeRequestDto.getToCurrency())
                .totalAmount(toBoxBalance.getBalance())
                .title("환전")
                .type(Transaction.Type.EXCHANGE)
                .category(categoryCacheService.getByName(CategoryType.EXCHANGE))
                .createdAt(transaction.getCreatedAt())
                .build();

        batchInsert.saveBatch(List.of(boxHistory1, boxHistory2));
    }

    private List<ExchangeTransaction> exchangeThroughKRW(Transaction transaction, Map<String, CurrentExchangeRateDto> currentExchangeRate, ExchangeRequestDto exchangeRequestDto) {
        //목표 -> 한화로 환전
        ExchangeTransaction txn1 = exchangeFromKRW(transaction, currentExchangeRate, exchangeRequestDto);

        BigDecimal amountKRW = txn1.getFromAmount();
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
        BigDecimal sellRate = currentExchangeRate.get(fromCurrency.name()).getSellRate();

        BigDecimal toAmount = exchangeRequestDto.getAmount();
        BigDecimal fromAmount;

        if (fromCurrency == CurrencyType.JPY) {
            // JPY는 100엔 기준이므로 나눠줘야 함
            fromAmount = sellRate
                    .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP) // 100으로 나누기
                    .multiply(toAmount);
        } else {
            // USD, EUR 같은 경우는 1 단위 기준
            fromAmount = sellRate.multiply(toAmount);
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
        BigDecimal buyRate = currentExchangeRate.get(toCurrency.name()).getBuyRate();

        BigDecimal toAmount = exchangeRequestDto.getAmount();
        BigDecimal fromAmount;

        if (toCurrency == CurrencyType.JPY) {
            // JPY는 100엔 기준
            fromAmount = buyRate
                    .divide(BigDecimal.valueOf(100)) // div 100
                    .multiply(toAmount);
        } else {
            // USD, EUR 같은 경우는 1 단위 기준
            fromAmount = buyRate.multiply(toAmount);
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
        boxMemberService.validateExchangePermission(box, user);

        BigDecimal amount = exchangeRequestDto.getAmount();
        BigDecimal ten = BigDecimal.TEN;
        BigDecimal minExchange = BigDecimal.valueOf(100);

        // 10단위 확인
        if (amount.remainder(ten).compareTo(BigDecimal.ZERO) != 0) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "10 단위로만 환전 가능합니다.");
        }

        // 최소 환전금액 확인
        if (amount.compareTo(minExchange) < 0) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "최소 환전금액보다 작게 환전할 수 없습니다. " + minExchange);
        }

    }

    public List<ExchangeTransaction> getExchangeTransactions(Long transactionId) {
        return exchangeRepository.findAllByTransactionId(transactionId);
    }

}
