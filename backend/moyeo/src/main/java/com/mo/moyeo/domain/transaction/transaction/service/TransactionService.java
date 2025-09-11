package com.mo.moyeo.domain.transaction.transaction.service;

import com.mo.moyeo.domain.box.entity.Box;
import com.mo.moyeo.domain.currency.service.CurrencyService;
import com.mo.moyeo.domain.transaction.exchange.dto.ExchangeRequestDto;
import com.mo.moyeo.domain.transaction.transaction.entity.Transaction;
import com.mo.moyeo.domain.transaction.transaction.repository.TransactionRepository;
import com.mo.moyeo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public Transaction makeExchangeTransaction(Box box, User user){
        Transaction transaction = Transaction.builder()
                .toBox(box)
                .fromBox(box)
                .user(user)
                .transactionType(Transaction.Type.EXCHANGE)
                .uuid(UUID.randomUUID().toString().replace("-", "").substring(0, 20))
                .build();
        return transactionRepository.save(transaction);
    }


}
