package com.mo.moyeo.domain.fcm.builder;

import com.mo.moyeo.domain.fcm.config.FcmConstants;
import com.mo.moyeo.domain.fcm.dto.FcmMessageDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * FCM 메시지 데이터 빌더
 */
@Component
public class FcmMessageBuilder {

    /**
     * 기본 알림 메시지 데이터 생성
     */
    public Map<String, String> buildBasicData(FcmMessageDTO message) {
        Map<String, String> data = new HashMap<>();
        data.put(FcmConstants.DATA_TITLE, message.getTitle());
        data.put(FcmConstants.DATA_BODY, message.getBody());
        data.put(FcmConstants.DATA_TYPE, message.getType());
        data.put(FcmConstants.DATA_TIMESTAMP, LocalDateTime.now().toString());
        if(message.getData() != null) data.putAll(message.getData());
        return data;
    }
}