package com.mo.moyeo.domain.auth.signup.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.auth.signup.dto.SignupSessionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignupSessionRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final class RedisKeys {
        private static final String SIGNUP_SESSION_PREFIX = "signup_session::";
        private static final String SMS_CODE_PREFIX = "signup_sms_code::";
        private static final String EMAIL_CODE_PREFIX = "signup_email_code::";
    }

    private static final class Timeouts {
        private static final Duration SESSION_TTL = Duration.ofMinutes(30);
        private static final Duration CODE_TTL = Duration.ofMinutes(5);
    }



    // === 회원가입 세션 관리 ===

    public void saveSignupSession(String sessionId, SignupSessionDTO session) {
        validateSessionId(sessionId);
        validateSession(session);

        try {
            String key = RedisKeys.SIGNUP_SESSION_PREFIX + sessionId;
            redisTemplate.opsForValue().set(key, session, Timeouts.SESSION_TTL);
            log.debug("회원가입 세션 저장 완료: {}", sessionId);
        } catch (Exception e) {
            log.error("회원가입 세션 저장 실패 - sessionId: {}", sessionId, e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "세션 저장에 실패했습니다");
        }
    }

    public Optional<SignupSessionDTO> findSignupSession(String sessionId) {
        validateSessionId(sessionId);

        try {
            String key = RedisKeys.SIGNUP_SESSION_PREFIX + sessionId;
            Object sessionObj = redisTemplate.opsForValue().get(key);

            if (sessionObj == null) {
                return Optional.empty();
            }

            return Optional.of((SignupSessionDTO) sessionObj);
        } catch (ClassCastException e) {
            log.error("세션 데이터 역직렬화 실패 - sessionId: {}", sessionId, e);
            deleteSignupSession(sessionId); // 손상된 데이터 정리
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "세션 데이터가 손상되었습니다");
        } catch (Exception e) {
            log.error("세션 조회 실패 - sessionId: {}", sessionId, e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "세션 조회에 실패했습니다");
        }
    }

    public SignupSessionDTO getSignupSession(String sessionId) {
        return findSignupSession(sessionId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "세션이 만료되었거나 존재하지 않습니다"
                ));
    }

    public void updateSignupSession(String sessionId, SignupSessionDTO updatedSession) {
        validateSessionId(sessionId);
        validateSession(updatedSession);

        // 기존 세션 존재 여부 확인
        if (!existsSignupSession(sessionId)) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "세션이 존재하지 않습니다");
        }

        updatedSession.setUpdatedAt(LocalDateTime.now());
        saveSignupSession(sessionId, updatedSession);
        log.debug("회원가입 세션 업데이트 완료: {}", sessionId);
    }

    public void deleteSignupSession(String sessionId) {
        validateSessionId(sessionId);

        try {
            String key = RedisKeys.SIGNUP_SESSION_PREFIX + sessionId;
            Boolean deleted = redisTemplate.delete(key);
            log.debug("회원가입 세션 삭제: {}, 성공: {}", sessionId, deleted);
        } catch (Exception e) {
            log.error("세션 삭제 실패 - sessionId: {}", sessionId, e);
            // 삭제는 실패해도 예외를 던지지 않음
        }
    }

    public boolean existsSignupSession(String sessionId) {
        validateSessionId(sessionId);

        try {
            String key = RedisKeys.SIGNUP_SESSION_PREFIX + sessionId;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("세션 존재 여부 확인 실패 - sessionId: {}", sessionId, e);
            return false;
        }
    }

    // === SMS 인증번호 관리 ===

    public void saveSmsVerificationCode(String phoneNumber, String code) {
        saveVerificationCode(phoneNumber, code, RedisKeys.SMS_CODE_PREFIX, "SMS");
    }

    public Optional<String> findSmsVerificationCode(String phoneNumber) {
        return findVerificationCode(phoneNumber, RedisKeys.SMS_CODE_PREFIX);
    }

    public String getSmsVerificationCode(String phoneNumber) {
        return findSmsVerificationCode(phoneNumber)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "SMS 인증번호가 만료되었거나 존재하지 않습니다"
                ));
    }

    public void deleteSmsVerificationCode(String phoneNumber) {
        deleteVerificationCode(phoneNumber, RedisKeys.SMS_CODE_PREFIX, "SMS");
    }

    // === 이메일 인증번호 관리 ===

    public void saveEmailVerificationCode(String email, String code) {
        saveVerificationCode(email, code, RedisKeys.EMAIL_CODE_PREFIX, "Email");
    }

    public Optional<String> findEmailVerificationCode(String email) {
        return findVerificationCode(email, RedisKeys.EMAIL_CODE_PREFIX);
    }

    public String getEmailVerificationCode(String email) {
        return findEmailVerificationCode(email)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "이메일 인증번호가 만료되었거나 존재하지 않습니다"
                ));
    }

    public void deleteEmailVerificationCode(String email) {
        deleteVerificationCode(email, RedisKeys.EMAIL_CODE_PREFIX, "Email");
    }

    // === 공통 메서드들 ===

    private void saveVerificationCode(String redisKey, String code, String prefix, String type) {
        validateSessionId(redisKey);
        validateVerificationCode(code);

        try {
            String key = prefix + redisKey;
            redisTemplate.opsForValue().set(key, code, Timeouts.CODE_TTL);
            log.debug("{} 인증번호 저장 완료 - sessionId: {}", type, redisKey);
        } catch (Exception e) {
            log.error("{} 인증번호 저장 실패 - sessionId: {}", type, redisKey, e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "인증번호 저장에 실패했습니다");
        }
    }

    private Optional<String> findVerificationCode(String redisKey, String prefix) {
        validateSessionId(redisKey);

        try {
            String key = prefix + redisKey;
            String code = (String) redisTemplate.opsForValue().get(key);
            return Optional.ofNullable(code);
        } catch (Exception e) {
            log.error("인증번호 조회 실패 - sessionId: {}, prefix: {}", redisKey, prefix, e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "인증번호 조회에 실패했습니다");
        }
    }

    private void deleteVerificationCode(String redisKey, String prefix, String type) {
        validateSessionId(redisKey);

        try {
            String key = prefix + redisKey;
            Boolean deleted = redisTemplate.delete(key);
            log.debug("{} 인증번호 삭제: {}, 성공: {}", type, redisKey, deleted);
        } catch (Exception e) {
            log.error("{} 인증번호 삭제 실패 - sessionId: {}", type, redisKey, e);
            // 삭제는 실패해도 예외를 던지지 않음
        }
    }

    // === 유효성 검증 메서드들 ===

    private void validateSessionId(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("세션 ID는 필수입니다");
        }
    }

    private void validateSession(SignupSessionDTO session) {
        if (session == null) {
            throw new IllegalArgumentException("세션 데이터는 필수입니다");
        }
    }

    private void validateVerificationCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("인증번호는 필수입니다");
        }
    }
}