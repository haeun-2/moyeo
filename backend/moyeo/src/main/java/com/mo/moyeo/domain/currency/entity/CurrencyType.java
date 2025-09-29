package com.mo.moyeo.domain.currency.entity;

public enum CurrencyType {
    CAD("CA"),
    CHF("CH"),
    CNY("CN"),
    EUR("EU"),
    GBP("GB"),
    JPY("JP"),
    KRW("KR"),
    USD("US");

    private final String flagEmoji;

    CurrencyType(String flagEmoji) {
        this.flagEmoji = flagEmoji;
    }

    public String getFlagEmoji() {
        return flagEmoji;
    }

    public static String getFlagByCurrency(CurrencyType currencyCode) {
        return currencyCode.getFlagEmoji();
    }
}
