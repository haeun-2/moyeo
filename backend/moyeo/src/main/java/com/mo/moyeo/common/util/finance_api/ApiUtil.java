package com.mo.moyeo.common.util.finance_api;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class ApiUtil {
    @Value("${MOYEO_API_KEY}")
    private String apiKeyInstance;

    private static final Random random = new Random();
    public static String apiKey;

    @PostConstruct
    public void init() {
        apiKey = apiKeyInstance;
    }

    public static Map<String, Object> createHeader(String apiName) {
        Map<String, Object> map = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        String currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        String uniqueNo = currentDate + currentTime + String.valueOf(random.nextInt(900000)+ 100000);//TODO : 랜덤번호 변경하기

        map.put("apiName", apiName);
        map.put("transmissionDate", currentDate);
        map.put("transmissionTime", currentTime);
        map.put("institutionCode", "00100");
        map.put("fintechAppNo", "001");
        map.put("apiServiceCode", apiName);
        map.put("institutionTransactionUniqueNo", uniqueNo);
        map.put("apiKey", apiKey);

        return map;
    }
}
