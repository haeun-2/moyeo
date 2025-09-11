package com.mo.moyeo.domain.auth.signup.service;

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
public class AccountConnectionService {

    @Value("${moyeo.api.auth_text}")
    private String AUTH_TEXT;

    @Value("${moyeo.api.base_url}")
    private String BASE_URL;

    @Value("${moyeo.api.user_search_endpoint}")
    private String USER_SEARCH_ENDPOINT;

    @Value("${moyeo.api.open_auth_endpoint}")
    private String OPEN_AUTH_ENDPOINT;

    @Value("${moyeo.api.check_auth_endpoint}")
    private String CHECK_AUTH_ENDPOINT;

    private final RestTemplate restTemplate;

    @Value("${moyeo.api.key}")
    private String apiKey;

    /**
     * 금융망 사용자 키 조회
     */
    protected String getUserKey(String email) {
        String url = BASE_URL + USER_SEARCH_ENDPOINT;

        Map<String, String> body = new HashMap<>();
        body.put("userId", email);
        body.put("apiKey", apiKey);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("사용자 키 조회 실패: 응답이 없습니다");
        }

        return (String) responseBody.get("userKey");
    }

    /**
     * 1원 인증 송금 요청
     */
    public void sendVerificationWon(String email, String accountNo) {
        String userKey = getUserKey(email);
        String url = BASE_URL + OPEN_AUTH_ENDPOINT;

        Map<String, Object> requestBody = createAuthRequestBody("openAccountAuth", userKey, accountNo, null);
        HttpEntity<Map<String, Object>> request = createHttpEntity(requestBody);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        log.debug("1원 인증 송금 응답: {}", response);
    }

    /**
     * 1원 인증 코드 검증
     */
    public String verifyWonCode(String email, String accountNo, String verificationCode) {
        String userKey = getUserKey(email);
        String url = BASE_URL + CHECK_AUTH_ENDPOINT;

        Map<String, Object> requestBody = createAuthRequestBody("checkAuthCode", userKey, accountNo, verificationCode);
        HttpEntity<Map<String, Object>> request = createHttpEntity(requestBody);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("인증 코드 검증 실패: 응답이 없습니다");
        }

        Map<String, Object> rec = (Map<String, Object>) responseBody.get("REC");
        if (rec == null) {
            throw new RuntimeException("인증 코드 검증 실패: REC 정보가 없습니다");
        }

        return (String) rec.get("status");
    }

    /**
     * 인증 요청 바디 생성
     */
    private Map<String, Object> createAuthRequestBody(String apiName, String userKey, String accountNo, String authCode) {
        Map<String, Object> headerMap = ApiUtil.createHeader(apiName);
        headerMap.put("userKey", userKey);

        Map<String, Object> body = new HashMap<>();
        body.put("Header", headerMap);
        body.put("accountNo", accountNo);
        body.put("authText", AUTH_TEXT);

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