package com.mo.moyeo.domain.bank.service;

import com.mo.moyeo.common.util.finance_api.ApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountVerificationService {

    @Value("${MOYEO_API_AUTH_TEXT}")
    private String AUTH_TEXT;

    @Value("${BASE_URL}")
    private String BASE_URL;

    @Value("${OPEN_AUTH_ENDPOINT}")
    private String OPEN_AUTH_ENDPOINT;

    @Value("${CHECK_AUTH_ENDPOINT}")
    private String CHECK_AUTH_ENDPOINT;

    private final RestTemplate restTemplate;

    /**
     * 1원 인증 송금 요청
     */
    public void sendWonCode(String userKey, String accountNo) {

        String url = BASE_URL + OPEN_AUTH_ENDPOINT;

        Map<String, Object> requestBody = createAuthRequestBody("openAccountAuth", userKey, accountNo, null);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, createHttpEntity(requestBody), Map.class);
        log.debug("1원 인증 송금 응답: {}", response);
    }

    /**
     * 1원 인증 코드 검증
     */
    public void verifyWonCode(String userKey, String accountNo, String verificationCode) {

        String url = BASE_URL + CHECK_AUTH_ENDPOINT;

        Map<String, Object> requestBody = createAuthRequestBody("checkAuthCode", userKey, accountNo, verificationCode);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, createHttpEntity(requestBody), Map.class);

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("인증 코드 검증 실패: 응답이 없습니다");
        }

        Map<String, Object> rec = (Map<String, Object>) responseBody.get("REC");
        if (rec == null) {
            throw new RuntimeException("인증 코드 검증 실패: REC 정보가 없습니다");
        }
    }

    /**
     * 인증 요청 바디 생성
     */
    private Map<String, Object> createAuthRequestBody(String apiName, String userKey, String accountNo, String authCode) {
        Map<String, Object> header = ApiUtil.createHeader(apiName);
        header.put("userKey", userKey);

        Map<String, Object> body = new HashMap<>(Map.of(
                "Header", header,
                "accountNo", accountNo,
                "authText", AUTH_TEXT
        ));

        if (authCode != null) {
            body.put("authCode", authCode);
        }

        return body;
    }

    /**
     * HTTP 엔티티 생성
     */
    private HttpEntity<Map<String, Object>> createHttpEntity(Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}