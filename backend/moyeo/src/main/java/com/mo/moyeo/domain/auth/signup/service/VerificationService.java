package com.mo.moyeo.domain.auth.signup.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {

    private static final SecureRandom random = new SecureRandom();

    private DefaultMessageService messageService;
    private final JavaMailSender javaMailSender;

    @Value("${coolsms.api.url}")
    private String apiUrl;

    @PostConstruct
    private void initMessageService() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, apiUrl);
    }

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.sender-number}")
    private String senderNumber;

    /**
     * 문자 인증 코드 보내기
     */
    public String sendSmsVerificationCode (String phoneNumber) {
        String code = generateVerificationCode();

        Message message = new Message();
        message.setFrom(senderNumber);
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

    private String generateVerificationCode() {
        int number = random.nextInt(1_000_000);
        return String.format("%06d", number);
    }
}
