package com.mo.moyeo.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ======================================
    // COMMON ERRORS (공통 에러)
    // ======================================

    /**
     * 400 BAD_REQUEST - 입력값 검증 오류
     */
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_400_01", "잘못된 입력값입니다."),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "COMMON_400_02", "필수 입력값이 누락되었습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "COMMON_400_03", "잘못된 타입의 값입니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),

    /**
     * 404 NOT_FOUND - 공통 리소스 없음
     */
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404_01", "요청한 리소스를 찾을 수 없습니다."),

    /**
     * 409 CONFLICT - 중복 리소스
     */
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "COMMON_409_01", "이미 존재하는 리소스입니다."),

    /**
     * 500 INTERNAL_SERVER_ERROR - 서버 공통 오류
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_500_01", "서버 내부 오류가 발생했습니다."),

    /**
     * 502
     */
    BAD_GATEWAY(HttpStatus.BAD_GATEWAY, "SERVER_503", "게이트웨이 오류가 발생했습니다."),

    /**
     * 503
     */
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "SERVER_503", "현재 서버를 사용할 수 없습니다."),

    // ======================================
    // AUTHENTICATION & AUTHORIZATION (인증/인가)
    // ======================================

    /**
     * 400 BAD_REQUEST - 인증 제공자 오류
     */
    INVALID_PROVIDER(HttpStatus.BAD_REQUEST, "AUTH_400_01", "지원하지 않는 Provider 입니다."),

    /**
     * 401 UNAUTHORIZED - 인증 실패
     */
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "AUTH_401_01", "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_401_02", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_401_03", "만료된 토큰입니다."),

    /**
     * 403 FORBIDDEN - 접근 권한 없음
     */
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_403_01", "접근 권한이 없습니다."),

    /**
     * Box Error Code
     */
    BOX_NOT_FOUND(HttpStatus.NOT_FOUND, "BOX_404_01", "요청한 박스를 찾을 수 없습니다."),


    // ======================================
    // BANK TRANSACTION (은행 입/출금)
    // ======================================

    /**
     * 400 BAD_REQUEST - 요청 제공자 오류
     */
    INVALID_ACCOUNT_NUMBER(HttpStatus.BAD_REQUEST, "BANK_400_01", "유효하지 않은 계좌번호 입니다."),
    TRANSFER_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "BANK_400_02", "이체 한도 초과입니다."),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "BANK_400_03", "잔액이 부족합니다."),

    /**
     *
     */
    LOCK_ACQUISITION_FAILED(HttpStatus.CONFLICT, "LOCK_409_01", "일시적 오류입니다. 잠시 후 다시 시도해주세요."),

    /**
     * 500 INTERNAL_SERVER_ERROR - 서버 공통 오류
     */
    BANK_TRANSFER_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BANK_500_01", "거래에 실패하였습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
