package com.mo.moyeo.domain.fcm.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.fcm.dto.FcmTokenResponse;
import com.mo.moyeo.domain.fcm.dto.FcmTokenUpsertRequest;
import com.mo.moyeo.domain.fcm.entity.FcmToken;
import com.mo.moyeo.domain.fcm.repository.FcmTokenRepository;
import com.mo.moyeo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;

    /**
     * 토큰 등록/갱신
     */
    @Transactional
    public FcmTokenResponse upsertToken(User user, FcmTokenUpsertRequest request) {

        Optional<FcmToken> tokenOpt = fcmTokenRepository.findByUserId(user.getId());
        FcmToken token;

        // 기존 토큰이 있는 경우
        if (tokenOpt.isPresent()) {

            // 토큰 값만 변경
            token = tokenOpt.get();
            token.updateDeviceToken(request.getDeviceToken());
        } else {    // 없다면 새로 생성
            token = FcmToken.builder()
                    .deviceToken(request.getDeviceToken())
                    .user(user)
                    .build();
        }

        // 저장
        FcmToken savedToken = fcmTokenRepository.save(token);

        return FcmTokenResponse.builder()
                .deviceToken(savedToken.getDeviceToken())
                .build();
    }

    /**
     * 유저 토큰 삭제
     */
    @Transactional
    public void deleteTokenByUserId(Long userId) {
        fcmTokenRepository.findByUserId(userId)
                .ifPresent(fcmTokenRepository::delete);
    }

    /**
     * 토큰 값으로 삭제
     */
    @Transactional
    public void deleteTokenByDeviceToken(String deviceToken) {
        fcmTokenRepository.findByDeviceToken(deviceToken)
                .ifPresent(fcmTokenRepository::delete);
    }

    /**
     * 사용자의 활성 토큰 값 조회
     */
    public FcmToken getActiveToken(Long userId) {
        return fcmTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.FCM_TOKEN_NOT_FOUND));
    }

    /**
     * 모여 박스 멤버 토큰 전체 조회
     */
    public List<String> getGroupMemberTokens(Long boxId) {
        return fcmTokenRepository.findDeviceTokensByBoxId(boxId);
    }
}