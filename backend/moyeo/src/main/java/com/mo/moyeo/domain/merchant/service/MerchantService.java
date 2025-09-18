package com.mo.moyeo.domain.merchant.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.merchant.entity.Merchant;
import com.mo.moyeo.domain.merchant.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MerchantService {
    private final MerchantRepository merchantRepository;

    public Merchant getMerchantById(Long merchantId){
        return merchantRepository.findById(merchantId)
                .orElseThrow(()->new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
