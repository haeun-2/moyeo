package com.mo.moyeo.domain.transaction.transfer.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.common.util.batch.BatchInsert;
import com.mo.moyeo.domain.box.entity.Box;
import com.mo.moyeo.domain.box.entity.BoxBalance;
import com.mo.moyeo.domain.box.service.BoxBalanceService;
import com.mo.moyeo.domain.box.service.BoxMemberService;
import com.mo.moyeo.domain.box.service.BoxService;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.currency.service.CurrencyService;
import com.mo.moyeo.domain.transaction.history.entity.BoxHistory;
import com.mo.moyeo.domain.transaction.transaction.entity.Transaction;
import com.mo.moyeo.domain.transaction.transaction.service.TransactionService;
import com.mo.moyeo.domain.transaction.transfer.dto.TransferRequest;
import com.mo.moyeo.domain.transaction.transfer.entity.TransferTransaction;
import com.mo.moyeo.domain.transaction.transfer.repository.TransferRepository;
import com.mo.moyeo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransferService {

    private final TransferRepository transferRepository;
    private final BoxService boxService;
    private final BoxMemberService boxMemberService;
    private final TransactionService transactionService;
    private final CurrencyService currencyService;
    private final BoxBalanceService boxBalanceService;
    private final BatchInsert batchInsert;

    public void transfer(User user, TransferRequest request) {
        // 요청 검증
        if (request.getFromBoxId().equals(request.getToBoxId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        Double amount = request.getAmount();
        CurrencyType currency = request.getCurrency();

        Box fromBox = boxService.getBoxById(request.getFromBoxId());
        validatePermission(user, fromBox); // 이체 권한 화인
        BoxBalance fromBoxBalance = boxBalanceService.findBoxBalanceByBoxIdAndCurrencyType(fromBox, currency);
        validateSufficientBalance(fromBoxBalance, amount); // 출금 박스 잔액 확인

        Box toBox = boxService.getBoxById(request.getToBoxId());
        BoxBalance toBoxBalance = boxBalanceService.findBoxBalanceByBoxIdAndCurrencyType(toBox, currency);

        // 입출금
        fromBoxBalance.decreaseBalance(amount);
        toBoxBalance.increaseBalance(amount);

        // 이체 트랜잭션 생성
        Transaction transaction = transactionService.makeTransferTransaction(fromBox, toBox, user);
        TransferTransaction transferTransaction = TransferTransaction.builder()
                .transaction(transaction)
                .currency(currencyService.getReferenceByType(currency))
                .amount(amount)
                .build();
        transferRepository.save(transferTransaction);

        // 입출금 히스토리 저장
        BoxHistory fromHistory = BoxHistory.builder()
                .box(fromBox)
                .transaction(transaction)
                .amount(-1.0 * amount)
                .currencyCode(currency)
                .totalAmount(fromBoxBalance.getBalance())
                .title(toBox.getBoxName())
                .type(Transaction.Type.TRANSFER)
                .build();

        BoxHistory toHistory = BoxHistory.builder()
                .box(toBox)
                .transaction(transaction)
                .amount(amount)
                .currencyCode(currency)
                .totalAmount(toBoxBalance.getBalance())
                .title(fromBox.getBoxName())
                .type(Transaction.Type.TRANSFER)
                .build();

        batchInsert.saveBatch(List.of(fromHistory, toHistory));
    }

    private void validatePermission(User user, Box box) {
        if (box.isPersonal() && !box.getOwnerId().equals(user.getId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED, "권한이 없습니다.");
        }

        if (!box.isPersonal() && !boxMemberService.getMyPermission(box.getId(), user.getId()).getCanTransfer()) {
            throw new CustomException(ErrorCode.ACCESS_DENIED, "권한이 없습니다.");
        }
    }

    private void validateSufficientBalance(BoxBalance boxBalance, Double amount) {
        if (boxBalance.getBalance() < amount) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "잔액이 부족합니다.");
        }
    }

}
