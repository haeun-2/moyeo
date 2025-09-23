package com.mo.moyeo.domain.auth.signup.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.bank.service.AccountVerificationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {

    private static final SecureRandom random = new SecureRandom();

    private DefaultMessageService messageService;
    private final JavaMailSender javaMailSender;
    private final AccountVerificationService accountVerificationService;


    @Value("${USER_SEARCH_ENDPOINT}")
    private String USER_SEARCH_ENDPOINT;

    @Value("${BASE_URL}")
    private String MOYEO_BASE_URL;

    @Value("${MOYEO_API_KEY}")
    private String MOYEO_API_KEY;

    @Value("${SMS_API_URL}")
    private String SMS_API_URL;

    @Value("${SMS_API_KEY}")
    private String SMS_API_KEY;

    @Value("${SMS_API_SECRET}")
    private String SMS_API_SECRET;

    @Value("${SMS_SENDER_NUMBER}")
    private String SMS_SENDER_NUMBER;

    @PostConstruct
    private void initMessageService() {
        this.messageService = NurigoApp.INSTANCE.initialize(SMS_API_KEY, SMS_API_SECRET, SMS_API_URL);
    }

    private final RestTemplate restTemplate;

    /**
     * 1원 인증 코드 보내기
     */
    public void sendWonCode(String email, String accountNo) {
        String userKey = getUserKeyFromApi(email);
        accountVerificationService.sendWonCode(userKey, accountNo);
    }

    /**
     * 1원 인증 코드 검증
     */
    public void verifyWonCode(String email, String accountNo, String verificationCode) {
        String userKey = getUserKeyFromApi(email);
        accountVerificationService.verifyWonCode(userKey, accountNo, verificationCode);
    }

    /**
     * 문자 인증 코드 보내기
     */
    public String sendSmsVerificationCode (String phoneNumber) {
        String code = generateVerificationCode();

        Message message = new Message();
        message.setFrom(SMS_SENDER_NUMBER);
        message.setTo(phoneNumber);
        message.setText(String.format("[MOYEO] 인증번호 [%s]를 입력해주세요.", code));

        try {
            messageService.send(message);
            return code;

        } catch (Exception exception) {
            log.error("인증 SMS 전송 실패 : {}", exception.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "인증 SMS 전송 실패");
        }
    }

    /**
     * 메일 인증 코드 보내기
     */
    public String sendEmailVerificationCode(String email) {
        String code = generateVerificationCode();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[모여] 이메일 인증코드");
        message.setText(String.format("[MOYEO] 인증코드는 [%s]입니다.", code));

        try {
            javaMailSender.send(message);
            return code;
        } catch (Exception exception) {
            log.error("인증 메일 전송 실패 : {}", exception.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "인증 메일 전송 실패");
        }
    }

    /**
     * 인증 코드 생성
     */
    private String generateVerificationCode() {
        int number = random.nextInt(1_000_000);
        return String.format("%06d", number);
    }

    /**
     * 금융망 API에서 사용자 키 조회
     */
    public String getUserKeyFromApi(String email) {
        String url = MOYEO_BASE_URL + USER_SEARCH_ENDPOINT;

        Map<String, String> body = Map.of(
                "userId", email,
                "apiKey", MOYEO_API_KEY
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(url, new HttpEntity<>(body), Map.class);

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("사용자 키 조회 실패: 응답이 없습니다");
        }

        return (String) responseBody.get("userKey");
    }
}
