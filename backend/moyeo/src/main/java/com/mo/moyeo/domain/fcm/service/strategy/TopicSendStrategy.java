package com.mo.moyeo.domain.fcm.service.strategy;

import com.google.firebase.messaging.*;
import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.fcm.builder.FcmMessageBuilder;
import com.mo.moyeo.domain.fcm.dto.FcmMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TopicSendStrategy {

    private final FirebaseMessaging firebaseMessaging;
    private final FcmMessageBuilder fcmMessageBuilder;

    public void send(String topicName, FcmMessageDTO messageDTO) {
        try {
            Map<String, String> data = fcmMessageBuilder.buildBasicData(messageDTO);

            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(messageDTO.getTitle())
                            .setBody(messageDTO.getBody())
                            .build())
                    .putAllData(data)
                    .setTopic(topicName)
                    .setAndroidConfig(getAndroidConfig())
                    .build();

            String response = firebaseMessaging.send(message);
            log.info("토픽 메시지 전송 성공: topic={}, messageId={}", topicName, response);

        } catch (FirebaseMessagingException e) {
            log.error("토픽 메시지 전송 실패: topic={}, error={}", topicName, e.getMessage());
            throw new CustomException(ErrorCode.FCM_SEND_FAILED);
        }
    }

    private AndroidConfig getAndroidConfig() {
        return AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                        .setSound("default")
                        .build())
                .build();
    }
}