package com.mo.moyeo.domain.transaction.exchange.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeResponseDto {

    private Header header;
    private Rec rec;

    @Getter
    @Setter
    public static class Header {
        private String responseCode;
        private String responseMessage;
        private String apiName;
        private String transmissionDate;
        private String transmissionTime;
        private String institutionCode;
        private String apiKey;
        private String apiServiceCode;
        private String institutionTransactionUniqueNo;
    }

    @Getter
    @Setter
    public static class Rec {
        private ExchangeCurrency exchangeCurrency;
        private AccountInfo accountInfo;
    }

    @Getter
    @Setter
    public static class ExchangeCurrency {
        private String amount;
        private String exchangeRate;
        private String currency;
        private String currencyName;
    }

    @Getter
    @Setter
    public static class AccountInfo {
        private String accountNo;
        private String amount;
        private String balance;
    }
}
