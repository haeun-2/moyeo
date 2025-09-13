package com.mo.moyeo.domain.transaction.category.entity;

public enum CategoryType {
    DEPOSIT("입금"),
    WITHDRAW("출금"),
    EXCHANGE("환전"),
    TRANSPORTATION("교통"),
    ACCOMMODATION("숙박"),
    FOOD("음식"),
    TOUR_ACTIVITY("관광/액티비티"),
    SHOPPING("쇼핑"),
    OTHER("기타");

    private final String label;

    CategoryType(String name) {
        this.label = name;
    }

    public String getLabel() {
        return label;
    }
}