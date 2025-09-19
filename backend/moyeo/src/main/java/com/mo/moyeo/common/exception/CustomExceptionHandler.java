package com.mo.moyeo.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e, HttpServletRequest request) {

        String requestURI = request.getRequestURI();

        log.error("=== CustomException 발생 ===");
        log.error("에러 코드: {}", e.getErrorCode());
        log.error("에러 메시지: {}", e.getMessage());
        log.error("요청 URL: {}", requestURI);
        e.printStackTrace();
        ErrorCode errorCode = e.getErrorCode();
        String message = e.getCustomMessage() != null ? e.getCustomMessage() : errorCode.getMessage();

        return ResponseEntity.status(errorCode.getStatus().value())
                .body(ErrorResponse.builder()
                        .status(errorCode.getStatus().value())
                        .code(errorCode.getCode())
                        .message(message)
                        .build());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    protected ResponseEntity<ErrorResponse> handleHttpClientError(HttpClientErrorException e, HttpServletRequest request) {
        HttpStatusCode status = e.getStatusCode();
        ErrorCode errorCode = switch (status.value()) {
            case 400 -> ErrorCode.BAD_REQUEST;
            case 401 -> ErrorCode.UNAUTHORIZED_ACCESS;
            case 403 -> ErrorCode.ACCESS_DENIED;
            case 404 -> ErrorCode.RESOURCE_NOT_FOUND;
            default -> ErrorCode.INTERNAL_SERVER_ERROR;
        };

        log.error("클라이언트 에러 발생 - 상태코드: {}, URL: {}", status.value(), request.getRequestURI());
        return ErrorResponse.toResponseEntity(errorCode);
    }

    @ExceptionHandler(HttpServerErrorException.class)
    protected ResponseEntity<ErrorResponse> handleHttpServerError(HttpServerErrorException e, HttpServletRequest request) {
        HttpStatusCode status = e.getStatusCode();
        ErrorCode errorCode = switch (status.value()) {
            case 502 -> ErrorCode.BAD_GATEWAY;
            case 503 -> ErrorCode.SERVICE_UNAVAILABLE;
            default -> ErrorCode.INTERNAL_SERVER_ERROR;
        };

        log.error("서버 에러 발생 - 상태코드: {}, URL: {}", status.value(), request.getRequestURI());
        return ErrorResponse.toResponseEntity(errorCode);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e, HttpServletRequest request) {

        CustomException customException = new CustomException(ErrorCode.BAD_REQUEST, "잘못된 JSON 형식입니다");
        return handleCustomException(customException, request);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        log.error("=== 일반 Exception 발생 ===");
        log.error("예외 타입: {}", e.getClass().getSimpleName());
        log.error("예외 메시지: {}", e.getMessage(), e);
        log.error("요청 URL: {}", requestURI);

        return ErrorResponse.toResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleValidationExceptions(BindException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        log.error("=== Validation Error 발생 ===");
        log.error("요청 URL: {}", requestURI);
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            log.error("예외 필드: {}", fieldError.getField());
            log.error("예외 메시지: {}", fieldError.getDefaultMessage());
        });


        return ErrorResponse.toResponseEntity(ErrorCode.BAD_REQUEST,
                e.hasFieldErrors() ? e.getFieldError().getDefaultMessage() : e.getGlobalError().getDefaultMessage());
    }

    @ExceptionHandler({NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(Exception e, HttpServletRequest request) {
        return ErrorResponse.toResponseEntity(ErrorCode.RESOURCE_NOT_FOUND, "잘못된 요청 경로 혹은 요청 메서드입니다.");
    }
}
