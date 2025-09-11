package com.mo.moyeo.domain.exchange.rate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ExchangeRateResponse {

    @JsonProperty("Header")
    private Header header;

    @JsonProperty("REC")
    private List<Rec> rec;

    @Data
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

    @Data
    public static class Rec {
        private Long id;
        private String currency;

        @JsonProperty("exchangeRate")
        private String exchangeRate;

        private String exchangeMin;
        private String created;
    }
}
