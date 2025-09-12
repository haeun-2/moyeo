package com.mo.moyeo.domain.transaction.bank.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
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

    /**
     * 모여머니 충전
     */
    @Transactional
    public BankTransactionResponse charge(User user, DepositRequest request) {
        return processBankTransaction(user, Double.valueOf(request.getBalance()), Transaction.Type.DEPOSIT);
    }

    /**
     * 모여머니 현금화
     */
    @Transactional
    public BankTransactionResponse discharge(User user, WithdrawRequest request) {
        return processBankTransaction(user, request.getBalance(), Transaction.Type.WITHDRAW);
    }

    /**
     * 트랜잭션 생성 & 거래 처리
     */
    private BankTransactionResponse processBankTransaction(User user, Double amount, Transaction.Type type) {
        Box box = boxService.getBoxByUserId(user.getId());

        // 트랜잭션 생성
        Transaction transaction = (type == Transaction.Type.DEPOSIT)
                ? transactionService.makeDepositTransaction(box, user)
                : transactionService.makeWithdrawalTransaction(box, user);

        // 세부 뱅크 트랜잭션 생성
        BankTransaction bankTransaction = makeBankTransaction(transaction, user, amount);

        try {
            // 박스 잔액 조회
            BoxBalance boxBalance = boxBalanceService.findBoxBalanceByBoxIdAndCurrencyType(box, CurrencyType.KRW);

            if(type.equals(Transaction.Type.DEPOSIT)) {
                bankApiService.deposit(user, amount, bankTransaction);
            } else {
                if(boxBalance.getBalance() < amount) throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE);

                bankApiService.withdraw(user, amount, bankTransaction);
            }

            // 트랜잭션 상태 성공으로 변경
            bankTransaction.updateStatus(BankTransaction.Status.COMPLETED);

            // 잔액 반영
            if (type == Transaction.Type.DEPOSIT) {
                boxBalance.increaseBalance(amount);
            } else {
                boxBalance.decreaseBalance(amount);
                amount = -amount;
            }

            // 히스토리 기록
            BoxHistory history = makeBoxHistory(box, transaction, amount, boxBalance, type);
            boxHistoryService.saveHistory(history);

            return BankTransactionResponse.builder().isSuccess(true).build();

        } catch (Exception e) {
            bankTransaction.updateStatus(BankTransaction.Status.FAILED);
            return buildErrorResponse(bankTransaction, e);
        }
    }



    // === 기타 유틸리티 메서드 ========================================

    /**
     * BankTransaction 생성
     */
    public BankTransaction makeBankTransaction(Transaction transaction, User user, Double amount) {
        BankTransaction bankTransaction = BankTransaction.builder()
                .transaction(transaction)
                .bank(bankService.getConnectedBank(user))
                .bankAccount(user.getConnectedBankAccount())
                .amount(amount)
                .build();

        return bankTransactionRepository.save(bankTransaction);
    }

    /**
     * BankTransaction용 BoxHistory 생성
     */
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

    /**
     * 에러 응답 생성
     */
    private BankTransactionResponse buildErrorResponse(BankTransaction bankTransaction, Exception e) {
        String errorMessage = null;
        if (e instanceof CustomException customEx) {
            errorMessage = customEx.getMessage();
            bankTransaction.updateErrorCode(String.valueOf(customEx.getErrorCode()));
        }

        return BankTransactionResponse.builder()
                .isSuccess(false)
                .errorMessage(errorMessage)
                .build();
    }
}