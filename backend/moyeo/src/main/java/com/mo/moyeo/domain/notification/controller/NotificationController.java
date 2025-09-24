package com.mo.moyeo.domain.notification.controller;

import com.mo.moyeo.common.paging.PageResponse;
import com.mo.moyeo.domain.auth.security.dto.CustomUserDetails;
import com.mo.moyeo.domain.notification.dto.NotificationResponse;
import com.mo.moyeo.domain.notification.dto.NotificationSearchCondition;
import com.mo.moyeo.domain.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@Tag(name = "NotificationController", description = "알림 관련 기능을 제공합니다.")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "유저 알림 목록 조회", description = "유저가 받은 알림을 전체 조회합니다.")
    public ResponseEntity<PageResponse<NotificationResponse>> getAllNotifications(@AuthenticationPrincipal CustomUserDetails userDetails, @ModelAttribute @Valid NotificationSearchCondition condition) {

        PageResponse<NotificationResponse> responses = notificationService.findAllByUserId(userDetails.getUser().getId(), condition.toPageable());
        return ResponseEntity.ok(responses);
    }
}
