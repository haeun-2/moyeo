package com.mo.moyeo.domain.transaction.bank.service;

import com.mo.moyeo.domain.bank.service.BankService;
import com.mo.moyeo.domain.box.entity.Box;
import com.mo.moyeo.domain.box.entity.BoxBalance;
import com.mo.moyeo.domain.box.service.BoxBalanceService;
import com.mo.moyeo.domain.box.service.BoxService;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.transaction.bank.dto.*;
import com.mo.moyeo.domain.transaction.bank.entity.BankTransaction;
import com.mo.moyeo.domain.transaction.bank.repository.BankTransactionRepository;
import com.mo.moyeo.domain.transaction.history.entity.BoxHistory;
import com.mo.moyeo.domain.transaction.history.service.BoxHistoryService;
import com.mo.moyeo.domain.transaction.transaction.entity.Transaction;
import com.mo.moyeo.domain.transaction.transaction.service.TransactionService;
import com.mo.moyeo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BankTransactionService {

    private final BankTransactionRepository bankTransactionRepository;

    private final BoxBalanceService boxBalanceService;
    private final TransactionService transactionService;
    private final BoxService boxService;
    private final BankService bankService;
    private final BankApiService bankApiService;
    private final BoxHistoryService boxHistoryService;

    private final String TITLE = "연결 계좌";
    private static final CurrencyType DEFAULT_CURRENCY = CurrencyType.KRW;

    public BankTransaction makeBankTransaction(Transaction transaction, User user, Double amount) {
        BankTransaction bankTransaction = BankTransaction.builder()
                .transaction(transaction)
                .bank(bankService.getConnectedBank(user))
                .bankAccount(user.getConnectedBankAccount())
                .amount(amount)
                .build();

        return bankTransactionRepository.save(bankTransaction);
    }

    @Transactional
    public DepositResponse charge(User user, DepositRequest request) {

        Box box = boxService.getBoxByUserId(user.getId());

        // 트랜잭션 생성
        Transaction transaction = transactionService.makeDepositTransaction(box, user);
        BankTransaction bankTransaction = makeBankTransaction(transaction, user, Double.valueOf(request.getBalance()));

        BoxBalance boxBalance = boxBalanceService.findBoxBalanceByBoxIdAndCurrencyType(box, CurrencyType.KRW);
        boxBalance.increaseBalance(Double.valueOf(request.getBalance()));

        BoxHistory boxHistory = makeBoxHistory(box, transaction, Double.valueOf(request.getBalance()), boxBalance, Transaction.Type.DEPOSIT);
        boxHistoryService.saveHistory(boxHistory);

        bankApiService.deposit(user, request, bankTransaction);

        return DepositResponse.builder().isSuccess(true).build();
    }

    @Transactional
    public WithdrawResponse discharge(User user, WithdrawRequest request) {

        Box box = boxService.getBoxByUserId(user.getId());

        // 트랜잭션 생성
        Transaction transaction = transactionService.makeWithdrawalTransaction(box, user);
        BankTransaction bankTransaction = makeBankTransaction(transaction, user, Double.valueOf(request.getBalance()));

        BoxBalance boxBalance = boxBalanceService.findBoxBalanceByBoxIdAndCurrencyType(box, CurrencyType.KRW);
        boxBalance.decreaseBalance(Double.valueOf(request.getBalance()));

        BoxHistory boxHistory = makeBoxHistory(box, transaction, -Double.valueOf(request.getBalance()), boxBalance, Transaction.Type.WITHDRAW);
        boxHistoryService.saveHistory(boxHistory);

        bankApiService.withdraw(user, request, bankTransaction);

        return WithdrawResponse.builder().isSuccess(true).build();
    }

    private BoxHistory makeBoxHistory(Box box, Transaction transaction, Double amount, BoxBalance boxBalance, Transaction.Type type) {
        return BoxHistory.builder()
                .box(box)
                .transaction(transaction)
                .createdAt(transaction.getCreatedAt())
                .amount(amount)
                .currencyCode(DEFAULT_CURRENCY)
                .totalAmount(boxBalance.getBalance())
                .title(TITLE)
                .type(type)
                .build();
    }
}