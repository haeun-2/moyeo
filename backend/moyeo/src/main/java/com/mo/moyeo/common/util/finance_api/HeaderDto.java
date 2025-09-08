package com.mo.moyeo.common.util.finance_api;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeaderDto {
    private String apiName;
    private String transmissionDate;
    private String transmissionTime;

    @Builder.Default
    private String institutionCode = "00100";
    @Builder.Default
    private String fintechAppNo = "001";

    private String apiServiceCode;

    private String institutionTransactionUniqueNo;

    private String apiKey;


}