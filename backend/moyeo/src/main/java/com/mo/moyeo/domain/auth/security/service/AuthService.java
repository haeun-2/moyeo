package com.mo.moyeo.domain.auth.security.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.auth.security.config.JwtProperties;
import com.mo.moyeo.domain.auth.security.dto.*;
import com.mo.moyeo.domain.auth.security.util.JwtUtil;
import com.mo.moyeo.domain.user.entity.User;
import com.mo.moyeo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final JwtRedisService jwtRedisService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인 처리
     */
    public LoginResponse login(LoginRequest request) {
        try {
            // 사용자 조회
            User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                    .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 사용자입니다."));

            // fid 검증
            if (!passwordEncoder.matches(request.getFid(), user.getFid())) {
                throw new CustomException(ErrorCode.BAD_REQUEST, "fid가 일치하지 않습니다.");
            }

            // JWT 토큰 생성
            GeneratedTokenDTO tokens = jwtUtil.generateToken(user.getId(), user.getRole().toString());

            // Redis에 refresh token 저장
            jwtRedisService.saveRefreshToken(user.getId(), tokens.getRefreshToken());

            return LoginResponse.builder()
                    .accessToken(tokens.getAccessToken())
                    .refreshToken(tokens.getRefreshToken())
                    .build();

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생: {}", e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 로그아웃 처리
     */
    public void logout(Long userId) {
        // refresh token 제거
        jwtRedisService.removeRefreshToken(userId);

        // access token 블랙리스트 처리
    }

    /**
     * 리프레시 토큰 재발급
     */
    public RefreshResponse refresh(RefreshRequest request) {
        try {

            String refreshToken = request.getRefreshToken();

            if (refreshToken == null) {
                throw new CustomException(ErrorCode.BAD_REQUEST, "전달된 토큰 정보가 없습니다.");
            }

            // 토큰 유효성 검사 (만료 여부 포함)
            if (!jwtUtil.verifyToken(refreshToken)) {
                throw new CustomException(ErrorCode.EXPIRED_TOKEN);
            }

            // refresh token에서 유저 식별자, 권한 추출
            Long userId = jwtUtil.getUserId(refreshToken);
            String role = jwtUtil.getRole(refreshToken);

            // Redis에서 저장된 refresh token 조회
            String storedRefreshToken = jwtRedisService.getRefreshToken(userId);
            // 클라이언트에서 온 토큰과 Redis에 저장된 토큰 비교
            if (!refreshToken.equals(storedRefreshToken)) {
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }

            // 새로운 access token 발급
            String newAccessToken = jwtUtil.generateAccessToken(userId, role);

            long remainTimeMs = jwtUtil.getRemainingExpirationTime(refreshToken);
            long refreshTokenTotalValidity = jwtProperties.getRefreshTokenExpirationMs();
            double remainRatio = remainTimeMs / (double) refreshTokenTotalValidity;

            if (remainRatio <= 0.2) {
                String newRefreshToken = jwtUtil.generateRefreshToken(userId, role);

                // redis refresh token 교체
                jwtRedisService.saveRefreshToken(userId, newRefreshToken);

                return RefreshResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .build();
            } else {
                return RefreshResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public LoginResponse tempLogin(Long userId) {
        GeneratedTokenDTO tokens = jwtUtil.generateToken(userId, User.Role.USER.name());

        // Redis에 refresh token 저장
        jwtRedisService.saveRefreshToken(userId, tokens.getRefreshToken());

        return LoginResponse.builder()
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .build();
    }

}