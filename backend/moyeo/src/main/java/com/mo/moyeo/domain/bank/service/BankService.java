package com.mo.moyeo.domain.bank.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.bank.dto.BankInfoResponse;
import com.mo.moyeo.domain.bank.entity.Bank;
import com.mo.moyeo.domain.bank.repository.BankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;

    /**
     * 특정 은행 정보 조회
     */
    public BankInfoResponse getBankInfo(String bankCode) {

        Bank bank = bankRepository.findById(bankCode)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "주어진 코드에 해당하는 은행을 찾을 수 없습니다."));

        return BankInfoResponse.from(bank);
    }

    /**
     * 전체 은행 정보 목록 조회
     */
    public List<BankInfoResponse> getAllBankInfos() {
        return bankRepository.findAll()
                .stream()
                .map(BankInfoResponse::from)
                .toList();
    }
}
