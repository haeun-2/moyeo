package com.mo.moyeo.domain.transaction.bank.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.common.util.finance_api.ApiUtil;
import com.mo.moyeo.domain.auth.signup.service.EncryptionService;
import com.mo.moyeo.domain.transaction.bank.dto.BankTransferDTO;
import com.mo.moyeo.domain.transaction.bank.dto.DepositRequest;
import com.mo.moyeo.domain.transaction.bank.dto.WithdrawRequest;
import com.mo.moyeo.domain.transaction.bank.entity.BankTransaction;
import com.mo.moyeo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final String ENDPOINT = "updateDemandDepositAccountTransfer";

    /**
     * 박스로 입금 (연결 계좌 -> 법인 계좌 -> 박스)
     */
    public void deposit(User user, DepositRequest request, BankTransaction bankTransaction) {
        String userKey = encryptionService.decrypt(user.getConnectedBankKey());

        BankTransferDTO dto = BankTransferDTO.builder()
                .depositAccountNo(MOYEO_ACCOUNT)
                .depositTransactionSummary(String.format("user: %s", user.getName()))
                .withdrawalAccountNo(user.getConnectedBankAccount())
                .withdrawalTransactionSummary("모여")
                .transactionBalance(request.getBalance())
                .userKey(userKey)
                .build();

        executeTransfer(dto, bankTransaction);
    }

    /**
     * 박스에서 출금 (박스 -> 법인 계좌 -> 연결 계좌)
     */
    public void withdraw(User user, WithdrawRequest request, BankTransaction bankTransaction) {
        BankTransferDTO transferRequestBody = BankTransferDTO.builder()
                .depositAccountNo(user.getConnectedBankAccount())
                .depositTransactionSummary("모여")
                .withdrawalAccountNo(MOYEO_ACCOUNT)
                .withdrawalTransactionSummary(String.format("user: %s", user.getName()))
                .transactionBalance(request.getBalance())
                .userKey(MOYEO_USER_KEY)
                .build();

        executeTransfer(transferRequestBody, bankTransaction);
    }

    /**
     * 은행 이체 실행
     */
    private void executeTransfer(BankTransferDTO transferRequestBody, BankTransaction transaction) {
        String url = BASE_URL + DEMAND_DEPOSIT_URL + ENDPOINT;

        Map<String, Object> requestBody = createTransferRequestBody(transferRequestBody);
        HttpEntity<Map<String, Object>> apiRequest = createHttpEntity(requestBody);

        try {
            restTemplate.postForEntity(url, apiRequest, Map.class);
            transaction.updateStatus(BankTransaction.Status.COMPLETED);
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
        Map<String, Object> requestBody = createApiRequestBody(ENDPOINT, dto.getUserKey());
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
}
