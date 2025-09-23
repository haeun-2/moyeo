package com.mo.moyeo.domain.fcm.service;

import com.mo.moyeo.domain.box.box.entity.Box;
import com.mo.moyeo.domain.box.member.entity.BoxMember;
import com.mo.moyeo.domain.box.member.service.BoxMemberService;
import com.mo.moyeo.domain.fcm.config.FcmConstants;
import com.mo.moyeo.domain.fcm.dto.FcmMessageDTO;
import com.mo.moyeo.domain.fcm.entity.FcmToken;
import com.mo.moyeo.domain.notification.service.NotificationService;
import com.mo.moyeo.domain.transaction.history.entity.BoxHistory;
import com.mo.moyeo.domain.transaction.transaction.entity.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmMessagingService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final FcmTokenService fcmTokenService;
    private final FcmAsyncService fcmAsyncService;
    private final NotificationService notificationService;
    private final BoxMemberService boxMemberService;

    @Value("${fcm.default-topic-name}")
    private String defaultTopicName;

    /**
     * 일반 거래 알림
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendTransactionMessage(BoxHistory boxHistory) {
        FcmMessageDTO message = createTransactionMessage(boxHistory);
        sendMessage(boxHistory, message);
    }

    /**
     * 환전 거래 알림
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendExchangeMessage(BoxHistory fromHistory, BoxHistory toHistory) {
        FcmMessageDTO message = createExchangeMessage(fromHistory, toHistory);
        sendMessage(fromHistory, message);
    }

    /**
     * 토픽으로 메시지 전송
     */
    public void sendMessageByTopic(FcmMessageDTO message) {
        fcmAsyncService.sendTopicMessageAsync(defaultTopicName, message);
    }

    private void sendMessage(BoxHistory boxHistory, FcmMessageDTO message) {
        Box box = boxHistory.getBox();

        if (box.isPersonal()) {
            sendToUser(box.getOwnerId(), boxHistory, message);
        } else {
            sendToGroup(boxHistory, message);
        }
    }

    private void sendToUser(Long userId, BoxHistory boxHistory, FcmMessageDTO message) {
        FcmToken token = fcmTokenService.getActiveToken(userId);

        boolean shouldSaveNotification = !boxHistory.getType().equals(Transaction.Type.PAYMENT);

        if (shouldSaveNotification) {
            Runnable onSuccess = () -> saveNotification(userId, boxHistory.getTransaction().getId(), message);
            fcmAsyncService.sendMulticastMessageAsync(Collections.singletonList(token.getDeviceToken()), message, onSuccess);
        } else {
            fcmAsyncService.sendMulticastMessageAsync(Collections.singletonList(token.getDeviceToken()), message);
        }
    }

    private void sendToGroup(BoxHistory boxHistory, FcmMessageDTO message) {
        List<BoxMember> members = boxMemberService.getJoinedMembersByBoxId(boxHistory.getBox().getId());
        List<String> tokens = fcmTokenService.getGroupMemberTokens(boxHistory.getBox().getId());

        boolean shouldSaveNotification = !boxHistory.getType().equals(Transaction.Type.PAYMENT);

        if (shouldSaveNotification) {
            Runnable onSuccess = () -> {
                for (BoxMember member : members) {
                    saveNotification(member.getUser().getId(), boxHistory.getTransaction().getId(), message);
                }
            };
            fcmAsyncService.sendMulticastMessageAsync(tokens, message, onSuccess);
        } else {
            fcmAsyncService.sendMulticastMessageAsync(tokens, message);
        }
    }

    private void saveNotification(Long userId, Long transactionId, FcmMessageDTO message) {
        try {
            notificationService.createNotification(userId, transactionId, message);
            log.debug("사용자 {} Notification 저장 완료", userId);
        } catch (Exception e) {
            log.error("사용자 {} Notification 저장 실패: {}", userId, e.getMessage());
        }
    }

    private FcmMessageDTO createTransactionMessage(BoxHistory boxHistory) {

        return FcmMessageDTO.builder()
                .title(String.format("[%s] %s", boxHistory.getBox().getBoxName(), boxHistory.getType().getLabel()))
                .body(createTransactionBody(boxHistory))
                .data(createDataMap(boxHistory.getBox().getId()))
                .type(FcmConstants.MessageType.IMMEDIATE)
                .build();
    }

    private FcmMessageDTO createExchangeMessage(BoxHistory fromHistory, BoxHistory toHistory) {

        String body = String.format("""
            %s
            %.2f %s → %.2f %s
            """,
                toHistory.getCreatedAt().format(FORMATTER),
                fromHistory.getAmount().negate(),
                fromHistory.getCurrencyCode(),
                toHistory.getAmount(),
                toHistory.getCurrencyCode());

        return FcmMessageDTO.builder()
                .title(String.format("[%s] %s", fromHistory.getBox().getBoxName(), fromHistory.getType().getLabel()))
                .body(body)
                .data(createDataMap(fromHistory.getBox().getId()))
                .type(FcmConstants.MessageType.IMMEDIATE)
                .build();
    }

    private String createTransactionBody(BoxHistory boxHistory) {
        Transaction.Type type = boxHistory.getType();

        if (type.equals(Transaction.Type.TRANSFER) || type.equals(Transaction.Type.PAYMENT)) {
            return String.format("""
                %s
                %s
                %.2f %s
                잔액 %.2f %s
                """,
                    boxHistory.getCreatedAt().format(FORMATTER),
                    boxHistory.getTitle(),
                    boxHistory.getAmount(),
                    boxHistory.getCurrencyCode(),
                    boxHistory.getTotalAmount(),
                    boxHistory.getCurrencyCode());
        }

        // DEPOSIT, WITHDRAW - title 없음
        return String.format("""
            %s
            %.2f %s
            잔액 %.2f %s
            """,
                boxHistory.getCreatedAt().format(FORMATTER),
                boxHistory.getAmount(),
                boxHistory.getCurrencyCode(),
                boxHistory.getTotalAmount(),
                boxHistory.getCurrencyCode());
    }

    private Map<String, String> createDataMap(Long boxId) {
        Map<String, String> data = new HashMap<>();
        data.put(FcmConstants.DATA_BOX, String.valueOf(boxId));
        return data;
    }
}