package com.mo.moyeo.domain.fcm.service;

import com.mo.moyeo.domain.fcm.config.FcmConstants;
import com.mo.moyeo.domain.fcm.dto.FcmMessageDTO;
import com.mo.moyeo.domain.fcm.entity.FcmToken;
import com.mo.moyeo.domain.transaction.history.entity.BoxHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmMessagingService {

    private final FcmTokenService fcmTokenService;
    private final FcmAsyncService fcmAsyncService;

    @Value("${fcm.default-topic-name}")
    private String defaultTopicName;

    /**
     * 거래 알림
     */
    public void sendTransactionMessage(BoxHistory boxHistory, Long userId) {

        FcmMessageDTO message = FcmMessageDTO.builder()
                .title(boxHistory.getType().getLabel())
                .body(String.format("""
                      %s
                      %s -> %s
                      잔액 %f %s
                      """,
                        boxHistory.getCreatedAt(),
                        boxHistory.getBox().getBoxName(),
                        boxHistory.getTitle(),
                        boxHistory.getTotalAmount(),
                        boxHistory.getCurrencyCode()))
                .type(FcmConstants.MessageType.IMMEDIATE)
                .build();

        sendMessageByUserId(message, userId);
    }

    // ======= 기타 알림 메시지 템플릿 필요시 추가 =======

    /**
     * 토픽으로 메시지 전송
     */
    public void sendMessageByTopic(FcmMessageDTO message) {

        fcmAsyncService.sendTopicMessageAsync(defaultTopicName, message);
    }

    /**
     * 토큰으로 메시지 전송
     */
    public void sendMessageByToken(FcmMessageDTO message, List<String> tokens) {

        fcmAsyncService.sendMulticastMessageAsync(tokens, message);
    }

    /**
     * 사용자 ID로 메시지 전송
     */
    public void sendMessageByUserId(FcmMessageDTO message, Long userId) {
        FcmToken token = fcmTokenService.getActiveToken(userId);

        fcmAsyncService.sendMulticastMessageAsync(Collections.singletonList(token.getDeviceToken()), message);

        log.debug("사용자 {} 메시지 전송 완료.", userId);
    }
}