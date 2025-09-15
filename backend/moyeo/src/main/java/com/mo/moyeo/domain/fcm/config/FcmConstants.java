package com.mo.moyeo.domain.fcm.config;

/**
 * FCM 관련 상수
 */
public final class FcmConstants {

    private FcmConstants() {
        // 인스턴스화 방지
    }

    /**
     * 메시지 타입
     */
    public static final class MessageType {
        public static final String IMMEDIATE = "IMMEDIATE";
        public static final String SCHEDULED = "SCHEDULED";

        private MessageType() {}
    }

    /**
     * 데이터 필드 키
     */
    public static final String DATA_TITLE = "title";
    public static final String DATA_BODY = "body";
    public static final String DATA_TYPE = "type";
    public static final String DATA_TIMESTAMP = "timestamp";

    /**
     * 배치 크기
     */
    public static final int MAX_MULTICAST_SIZE = 500;
}