package com.mo.moyeo.domain.fcm.service;

import com.mo.moyeo.domain.fcm.dto.FcmMessageDTO;
import com.mo.moyeo.domain.fcm.service.strategy.MulticastSendStrategy;
import com.mo.moyeo.domain.fcm.service.strategy.TopicSendStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmAsyncService {

    private final TopicSendStrategy topicSendStrategy;
    private final MulticastSendStrategy multicastSendStrategy;

    /**
     * 일반 멀티캐스트 메시지 비동기 전송
     */
    @Async("fcmAsyncExecutor")
    public void sendMulticastMessageAsync(List<String> tokens, FcmMessageDTO message) {
        try {
            multicastSendStrategy.send(tokens, message);
            log.info("멀티캐스트 메시지 전송 완료: 토큰 수={}", tokens.size());

        } catch (Exception e) {
            log.error("멀티캐스트 메시지 전송 실패: 토큰 수={}, error={}", tokens.size(), e.getMessage());
        }
    }

    /**
     * 일반 토픽 메시지 비동기 전송
     */
    @Async("fcmAsyncExecutor")
    public void sendTopicMessageAsync(String topicName, FcmMessageDTO message) {
        try {
            topicSendStrategy.send(topicName, message);
            log.info("토픽 메시지 전송 완료: topic={}", topicName);

        } catch (Exception e) {
            log.error("토픽 메시지 전송 실패: topic={}, error={}", topicName, e.getMessage());
        }
    }
}