package com.mo.moyeo.domain.user.service;

import com.mo.moyeo.domain.bank.entity.Bank;
import com.mo.moyeo.domain.bank.service.BankService;
import com.mo.moyeo.domain.user.dto.AccountInfoResponse;
import com.mo.moyeo.domain.user.dto.UserInfoResponse;
import com.mo.moyeo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserInformationService {

    private final BankService bankService;

    public AccountInfoResponse getAccountInfo(User user) {

        Bank bank = bankService.getConnectedBank(user);

        return AccountInfoResponse.builder()
                .bankName(bank.getBankName())
                .bankLogoImg(bank.getLogoImg())
                .bankAccount(user.getConnectedBankAccount())
                .build();
    }

    public UserInfoResponse getUserInfo(User user) {

        return UserInfoResponse.builder().name(user.getName()).build();
    }
}
