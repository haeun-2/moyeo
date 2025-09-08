package com.mo.moyeo.domain.auth.signup.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.auth.signup.dto.*;
import com.mo.moyeo.domain.user.entity.User;
import com.mo.moyeo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final SignupSessionRedisService signupSessionRedisService;
    private final VerificationService verificationService;
    private final AccountConnectionService accountConnectionService;

    /**
     * 이메일 중복 확인 & 인증코드 전송
     */
    public VerificationResponse verifyEmail(EmailVerificationRequest request) {

        String email = request.getEmail();

        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE, "이미 가입된 이메일입니다.");
        }

        // 세션 생성
        String sessionId = UUID.randomUUID().toString();
        SignupSessionDTO signupSession = new SignupSessionDTO(sessionId);

        try {
            // 이메일 인증 코드 전송
            String code = verificationService.sendEmailVerificationCode(email);
            // 인증 코드 레디스에 저장
            signupSessionRedisService.saveEmailVerificationCode(email, code);

            // 세션 레디스에 저장
            signupSessionRedisService.saveSignupSession(sessionId, signupSession);

            return new VerificationResponse(sessionId);
        }  catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 이메일 인증코드 검증
     */
    public VerificationResponse verifyEmailCode(EmailVerificationConfirmRequest request) {

        String sessionId = request.getSessionId();
        SignupSessionDTO signupSession = signupSessionRedisService.getSignupSession(sessionId);

        String email = request.getEmail();

        // 이메일 인증 코드 확인
        String code = signupSessionRedisService.getEmailVerificationCode(email);

        if(!code.equals(request.getVerificationCode())) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "인증코드가 일치하지 않습니다.");
        }

        // 사용 완료 된 인증코드 redis에서 제거
        signupSessionRedisService.deleteEmailVerificationCode(email);

        // 세션 업데이트
        signupSession.setEmailVerified(true);
        signupSessionRedisService.updateSignupSession(sessionId, signupSession);

        return new VerificationResponse(sessionId);
    }

    /**
     * 전화번호 중복 확인 & 인증 문자 전송
     */
    public VerificationResponse verifyPhone(PhoneVerificationRequest request) {

        String sessionId = request.getSessionId();
        SignupSessionDTO signupSession = signupSessionRedisService.getSignupSession(sessionId);

        if(!signupSession.getEmailVerified()) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "유효하지 않은 인증 단계입니다.");
        }

        String phoneNumber = request.getPhoneNumber();

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE, "이미 가입된 전화번호입니다.");
        }

        // 인증번호 발송 후 redis에 저장
        String verificationCode = verificationService.sendSmsVerificationCode(phoneNumber);
        signupSessionRedisService.saveSmsVerificationCode(phoneNumber, verificationCode);

        return new VerificationResponse(sessionId);
    }

    /**
     * 문자 인증번호 검증
     */
    public VerificationResponse verifySmsCode(PhoneVerificationConfirmRequest request) {

        String sessionId = request.getSessionId();
        SignupSessionDTO signupSession = signupSessionRedisService.getSignupSession(sessionId);

        if(!signupSession.getEmailVerified()) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "유효하지 않은 인증 단계입니다.");
        }

        String phoneNumber = request.getPhoneNumber();

        String verificationCode = signupSessionRedisService.getSmsVerificationCode(phoneNumber);

        if (!request.getVerificationCode().equals(verificationCode)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "인증번호가 일치하지 않습니다.");
        }

        // 사용 완료 된 인증번호 redis에서 제거
        signupSessionRedisService.deleteSmsVerificationCode(phoneNumber);

        // 세션 업데이트
        signupSession.setPhoneNumberVerified(true);
        signupSessionRedisService.updateSignupSession(sessionId, signupSession);

        return new VerificationResponse(sessionId);
    }

    /**
     * 계좌정보 입력 및 1원인증 입금
     */
    public VerificationResponse verifyAccount(AccountVerificationRequest request) {

        String sessionId = request.getSessionId();
        SignupSessionDTO signupSession = signupSessionRedisService.getSignupSession(sessionId);

        if(!signupSession.getEmailVerified() || !signupSession.getPhoneNumberVerified()) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "유효하지 않은 인증 단계입니다.");
        }

        String bankAccount = request.getBankAccount();

        accountConnectionService.sendVerificationWon(request.getEmail(), bankAccount);

        return new VerificationResponse(sessionId);
    }

    /**
     * 계좌 1원인증 확인
     */
    public VerificationResponse verifyWonCode(AccountVerificationConfirmRequest request) {

        String sessionId = request.getSessionId();
        SignupSessionDTO signupSession = signupSessionRedisService.getSignupSession(sessionId);

        if(!signupSession.getEmailVerified() || !signupSession.getPhoneNumberVerified()) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "유효하지 않은 인증 단계입니다.");
        }

        accountConnectionService.verifyWonCode(request.getEmail(), request.getBankAccount(), request.getVerificationCode());

        // 세션 업데이트
        signupSession.setBankAccountVerified(true);
        signupSessionRedisService.updateSignupSession(sessionId, signupSession);

        return new VerificationResponse(sessionId);
    }

    /**
     * 회원가입 완료 처리
     */
    @Transactional
    public void completeSignup(SignupCompleteRequest request) {
        String sessionId = request.getSessionId();
        SignupSessionDTO signupSession = signupSessionRedisService.getSignupSession(sessionId);

        if(!signupSession.getEmailVerified() || !signupSession.getPhoneNumberVerified() || !signupSession.getBankAccountVerified()) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "유효하지 않은 인증 단계입니다.");
        }

        String bankKey = accountConnectionService.getUserKey(request.getEmail());

        // 암호화
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        String hashedBankKey = passwordEncoder.encode(bankKey);

        // 유저 객체 생성
        User newUser = User.from(request, hashedPassword, hashedBankKey);

        userRepository.save(newUser);

        signupSessionRedisService.deleteSignupSession(sessionId);
    }
}
