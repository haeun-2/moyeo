package com.mo.moyeo.domain.transaction.bank.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.common.util.finance_api.ApiType;
import com.mo.moyeo.common.util.finance_api.ApiUtil;
import com.mo.moyeo.domain.auth.signup.service.EncryptionService;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.transaction.bank.dto.BankTransferDTO;
import com.mo.moyeo.domain.transaction.bank.entity.BankTransaction;
import com.mo.moyeo.domain.user.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankApiService {

    private final RestTemplate restTemplate;

    private final EncryptionService encryptionService;

    @Value("${moyeo.api.base_url}")
    private String BASE_URL;

    @Value("${moyeo.api.endpoint.demand_deposit}")
    private String DEMAND_DEPOSIT_URL;

    @Value("${moyeo.api.account}")
    private String MOYEO_ACCOUNT;

    @Value("${moyeo.api.user_key}")
    private String MOYEO_USER_KEY;

    @Value("${FOREIGN_WITHDRAW_ENDPOINT}")
    private String FOREIGN_CURRENCY_WITHDRAWAL_ENDPOINT;
    @Value("${FOREIGN_DEPOSIT_ENDPOINT}")
    private String FOREIGN_CURRENCY_DEPOSIT_ENDPOINT ;
    @Value("${KOREAN_DEPOSIT_ENDPOINT}")
    private String KOREAN_DEPOSIT_ENDPOINT;
    @Value("${KOREAN_WITHDRAW_ENDPOINT}")
    private String KOREAN_WITHDRAW_ENDPOINT;
    @Value("${FOREIGN_CURRENCY_TRANSFER_ENDPOINT}")
    private String FOREIGN_CURRENCY_TRANSFER;
    @Value("${KOREAN_TRANSFER_ENDPOINT}")
    private String KOREAN_TRANSFER_ENDPOINT;

    /**
     * 박스로 입금 (연결 계좌 -> 법인 계좌 -> 박스)
     */
    public void deposit(User user, BigDecimal amount, BankTransaction bankTransaction) {
        String userKey = encryptionService.decrypt(user.getConnectedBankKey());

        BankTransferDTO dto = BankTransferDTO.builder()
                .depositAccountNo(MOYEO_ACCOUNT)
                .depositTransactionSummary(String.format("user: %s", user.getName()))
                .withdrawalAccountNo(user.getConnectedBankAccount())
                .withdrawalTransactionSummary("모여")
                .transactionBalance(amount)
                .userKey(userKey)
                .build();

        executeTransfer(dto);
    }

    /**
     * 박스에서 출금 (박스 -> 법인 계좌 -> 연결 계좌)
     */
    public void withdraw(User user, BigDecimal amount, BankTransaction bankTransaction) {
        BankTransferDTO transferRequestBody = BankTransferDTO.builder()
                .depositAccountNo(user.getConnectedBankAccount())
                .depositTransactionSummary("모여")
                .withdrawalAccountNo(MOYEO_ACCOUNT)
                .withdrawalTransactionSummary(String.format("user: %s", user.getName()))
                .transactionBalance(amount)
                .userKey(MOYEO_USER_KEY)
                .build();

        executeTransfer(transferRequestBody);
    }

    /**
     * 은행 이체 실행
     */
    public void executeTransfer(BankTransferDTO transferRequestBody) {
        String url = KOREAN_TRANSFER_ENDPOINT;

        Map<String, Object> requestBody = createTransferRequestBody(transferRequestBody);
        HttpEntity<Map<String, Object>> apiRequest = createHttpEntity(requestBody);

        try {
            restTemplate.postForEntity(url, apiRequest, Map.class);
        } catch (HttpClientErrorException e) {
            // HTTP 4xx 에러의 응답 body 추출
            handleHttpClientError(e);
        } catch (HttpServerErrorException e) {
            // HTTP 5xx 에러 처리
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 오류: " + e.getStatusCode());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "거래 실패: " + e.getMessage());
        }
    }

    /**
     * 법인 계좌 입금
     */
    public void deposit(String account, BigDecimal amount, CurrencyType currencyType) {
        if(currencyType == CurrencyType.KRW){
            executeForeignCurrencyTransfer(KOREAN_DEPOSIT_ENDPOINT, account, amount, ApiType.updateDemandDepositAccountDeposit);
        }else{
            executeForeignCurrencyTransfer(FOREIGN_CURRENCY_DEPOSIT_ENDPOINT, account, amount, ApiType.updateForeignCurrencyDemandDepositAccountDeposit);
        }
    }

    public void withdraw(String account, BigDecimal amount, CurrencyType currencyType){
        if(currencyType == CurrencyType.KRW){
            executeForeignCurrencyTransfer(KOREAN_WITHDRAW_ENDPOINT, account, amount, ApiType.updateDemandDepositAccountWithdrawal);
        }else{
            executeForeignCurrencyTransfer(FOREIGN_CURRENCY_WITHDRAWAL_ENDPOINT, account, amount, ApiType.updateForeignCurrencyDemandDepositAccountWithdrawal);
        }
    }


    /**
     * 법인 외화 계좌 입/출금 작업
     */
    private void executeForeignCurrencyTransfer(String endpoint, String account, BigDecimal amount, ApiType type) {
        log.debug("transfer {}, {}", endpoint, type.name());
        Map<String, Object> requestBody = createApiRequestBody(type.name(), MOYEO_USER_KEY);
        requestBody.put("accountNo", account);
        requestBody.put("transactionBalance", amount);
        requestBody.put("transactionSummary", "외화 <-> 외화");

        HttpEntity<Map<String, Object>> apiRequest = createHttpEntity(requestBody);

        try {
            restTemplate.postForEntity(endpoint, apiRequest, Map.class);
        } catch (HttpClientErrorException e) {
            // HTTP 4xx 에러의 응답 body 추출
            log.debug(e.getMessage());
            handleHttpClientError(e);
        } catch (HttpServerErrorException e) {
            // HTTP 5xx 에러 처리
            log.debug(e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 오류: " + e.getStatusCode());
        } catch (Exception e) {
            log.debug(e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "거래 실패: " + e.getMessage());
        }
    }

    /**
     * HTTP 4xx 에러 응답 처리
     */
    private void handleHttpClientError(HttpClientErrorException e) {
        try {
            String responseBody = e.getResponseBodyAsString();

            if (responseBody != null && !responseBody.isEmpty()) {
                // JSON 파싱하여 세부 에러 정보 추출
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> errorResponse = objectMapper.readValue(responseBody, Map.class);

                String responseCode = (String) errorResponse.get("responseCode");
                String responseMessage = (String) errorResponse.get("responseMessage");

                // 에러 코드별 세분화 처리
                ErrorCode errorCode = mapBankErrorToCustomError(responseCode);
                String message = responseMessage != null ? responseMessage : "은행 거래 실패";

                throw new CustomException(errorCode, String.format("%s: %s", message, responseCode));
            }
        } catch (JsonProcessingException jsonE) {
            // JSON 파싱 실패 시 기본 에러 처리
            throw new CustomException(ErrorCode.BANK_TRANSFER_FAILED, "은행 거래 실패: " + e.getStatusCode());
        }

        // 응답 body가 없거나 파싱 실패한 경우 기본 처리
        throw new CustomException(ErrorCode.BANK_TRANSFER_FAILED, "은행 거래 실패: " + e.getStatusCode());
    }

    /**
     * 은행 에러 코드를 내부 에러 코드로 매핑
     */
    private ErrorCode mapBankErrorToCustomError(String bankErrorCode) {
        if (bankErrorCode == null) {
            return ErrorCode.BANK_TRANSFER_FAILED;
        }

        return switch (bankErrorCode) {
            case "A1014" -> ErrorCode.INSUFFICIENT_BALANCE;  // 잔액 부족
            case "A1016", "A1017" -> ErrorCode.TRANSFER_LIMIT_EXCEEDED;  // 이체 한도 초과
            case "A1003" -> ErrorCode.INVALID_ACCOUNT_NUMBER;  // 계좌번호 오류
            default -> ErrorCode.BANK_TRANSFER_FAILED;  // 기타 오류
        };
    }

    /**
     * 이체 요청 바디 생성
     */
    private Map<String, Object> createTransferRequestBody(BankTransferDTO dto) {
        Map<String, Object> requestBody = createApiRequestBody(ApiType.updateDemandDepositAccountTransfer.name(), dto.getUserKey());
        requestBody.put("depositAccountNo", dto.getDepositAccountNo());
        requestBody.put("depositTransactionSummary", dto.getDepositTransactionSummary());
        requestBody.put("transactionBalance", dto.getTransactionBalance());
        requestBody.put("withdrawalAccountNo", dto.getWithdrawalAccountNo());
        requestBody.put("withdrawalTransactionSummary", dto.getWithdrawalTransactionSummary());

        return requestBody;
    }

    /**
     * 인증 요청 바디 생성
     */
    private Map<String, Object> createApiRequestBody(String apiName, String userKey) {
        Map<String, Object> headerMap = ApiUtil.createHeader(apiName);
        headerMap.put("userKey", userKey);

        Map<String, Object> body = new HashMap<>();
        body.put("Header", headerMap);

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

    public void paymentTransfer(@NotNull CurrencyType currencyType, BankTransferDTO bankTransferDTO) {

        if (currencyType == CurrencyType.KRW) {
            executeTransfer(bankTransferDTO);
        } else {
            Map<String, Object> requestBody = createApiRequestBody(ApiType.updateForeignCurrencyDemandDepositAccountTransfer.name(), MOYEO_USER_KEY);
            requestBody.put("depositAccountNo", bankTransferDTO.getDepositAccountNo());
            requestBody.put("depositTransactionSummary", bankTransferDTO.getDepositTransactionSummary());
            requestBody.put("transactionBalance", bankTransferDTO.getTransactionBalance());
            requestBody.put("withdrawalAccountNo", bankTransferDTO.getWithdrawalAccountNo());
            requestBody.put("withdrawalTransactionSummary", bankTransferDTO.getWithdrawalTransactionSummary());
            HttpEntity<Map<String, Object>> apiRequest = createHttpEntity(requestBody);
            log.debug("{}", requestBody);

            try {
                restTemplate.postForEntity(FOREIGN_CURRENCY_TRANSFER, apiRequest, Map.class);
            } catch (HttpClientErrorException e) {
                // HTTP 4xx 에러의 응답 body 추출
                log.debug(e.getMessage());
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "입력 오류: " + e.getStatusCode());
            } catch (HttpServerErrorException e) {
                // HTTP 5xx 에러 처리
                log.debug(e.getMessage());
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 오류: " + e.getStatusCode());
            } catch (Exception e) {
                log.debug(e.getMessage());
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "거래 실패: " + e.getMessage());
            }
        }
    }
}