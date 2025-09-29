package com.mo.moyeo.domain.auth.security.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token::";

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    /**
     * Redis 키 생성
     */
    private String getRefreshTokenKey(Long userId) {
        return REFRESH_TOKEN_PREFIX + userId;
    }

    /**
     * 리프레시 토큰 저장
     */
    public void saveRefreshToken(Long userId, String refreshToken) {
        try {
            String key = getRefreshTokenKey(userId);
            redisTemplate.opsForValue().set(key, refreshToken, Duration.ofMillis(refreshTokenExpirationMs));
            log.debug("유저 {}의 refresh token이 저장되었습니다.", userId);
        } catch (Exception e) {
            log.error("Refresh token 저장 실패 - userId: {}, error: {}", userId, e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 리프레시 토큰 조회
     */
    public String getRefreshToken(Long userId) {
        try {
            String key = getRefreshTokenKey(userId);
            Object tokenObj = redisTemplate.opsForValue().get(key);

            if (tokenObj == null) {
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }
            return tokenObj.toString();
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Refresh token 조회 실패 - userId: {}, error: {}", userId, e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 리프레시 토큰 제거
     */
    public void removeRefreshToken(Long userId) {
        try {
            String key = getRefreshTokenKey(userId);
            redisTemplate.delete(key);
            log.debug("유저 {}의 refresh token이 삭제되었습니다.", userId);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
