package com.mo.moyeo.domain.fcm.service.strategy;

import com.google.firebase.messaging.*;
import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.fcm.builder.FcmMessageBuilder;
import com.mo.moyeo.domain.fcm.config.FcmConstants;
import com.mo.moyeo.domain.fcm.dto.FcmMessageDTO;
import com.mo.moyeo.domain.fcm.service.TokenFailureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MulticastSendStrategy {

    private final FirebaseMessaging firebaseMessaging;
    private final TokenFailureService tokenFailureService;
    private final FcmMessageBuilder fcmMessageBuilder;

    public void send(List<String> tokens, FcmMessageDTO message) {
        if (tokens.isEmpty()) {
            log.warn("전송할 토큰이 없습니다.");
            return;
        }

        // 500개씩 배치 처리
        for (int i = 0; i < tokens.size(); i += FcmConstants.MAX_MULTICAST_SIZE) {
            List<String> batch = tokens.subList(i,
                    Math.min(i + FcmConstants.MAX_MULTICAST_SIZE, tokens.size()));
            sendBatch(batch, message);
        }
    }

    private void sendBatch(List<String> tokens, FcmMessageDTO messageDTO) {
        try {
            Map<String, String> data = fcmMessageBuilder.buildBasicData(messageDTO);

            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle(messageDTO.getTitle())
                            .setBody(messageDTO.getBody())
                            .build())
                    .putAllData(data)
                    .addAllTokens(tokens)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .build();

            BatchResponse response = firebaseMessaging.sendEachForMulticast(message);

            log.info("멀티캐스트 전송 결과 - 성공: {}, 실패: {}",
                    response.getSuccessCount(), response.getFailureCount());

            // 실패한 토큰 처리
            if (response.getFailureCount() > 0) {
                tokenFailureService.handleFailedTokens(tokens, response);
            }

        } catch (FirebaseMessagingException e) {
            log.error("멀티캐스트 전송 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.FCM_SEND_FAILED);
        }
    }
}