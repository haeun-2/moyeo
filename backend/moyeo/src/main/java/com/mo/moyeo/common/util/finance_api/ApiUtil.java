package com.mo.moyeo.common.util.finance_api;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ApiUtil {
    @Value("${moyeo.api.key}")
    private static String apiKey;

    public static HeaderDto createHeader(String apiName){
        LocalDateTime now = LocalDateTime.now();
        String currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        String uniqueNo = currentDate+currentTime+"123456";//TODO : 랜덤번호 변경하기

        return HeaderDto.builder()
                .apiName(apiName)
                .transmissionDate(currentDate)
                .transmissionTime(currentTime)
                .apiServiceCode(apiName)
                .institutionTransactionUniqueNo(uniqueNo)
                .apiKey(apiKey)
                .build();
    }
}
