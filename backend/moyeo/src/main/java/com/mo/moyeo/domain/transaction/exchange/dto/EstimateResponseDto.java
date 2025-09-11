package com.mo.moyeo.domain.transaction.exchange.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstimateResponseDto {

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
        private Currency currency;
        private ExchangeCurrency exchangeCurrency;
    }

    @Getter
    @Setter
    public static class ExchangeCurrency {
        private String amount;
        private String currency;
        private String currencyName;
    }

    @Getter
    @Setter
    public static class Currency {
        private String amount;
        private String currency;
        private String currencyName;
    }
}
