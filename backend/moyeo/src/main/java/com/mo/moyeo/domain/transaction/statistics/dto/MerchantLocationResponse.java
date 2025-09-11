package com.mo.moyeo.domain.transaction.statistics.dto;

import com.mo.moyeo.domain.merchant.entity.Merchant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MerchantLocationResponse {

    private String merchantName;
    private String merchantAddress;
    private Double merchantLat;
    private Double merchantLng;

    public static MerchantLocationResponse from(Merchant merchant) {
        return MerchantLocationResponse.builder()
                .merchantName(merchant.getName())
                .merchantAddress(merchant.getAddress())
                .merchantLat(merchant.getLocationLat())
                .merchantLng(merchant.getLocationLng())
                .build();
    }

    public static List<MerchantLocationResponse> from(List<Merchant> merchantList) {
        return merchantList.stream().map(MerchantLocationResponse::from).toList();
    }

}
