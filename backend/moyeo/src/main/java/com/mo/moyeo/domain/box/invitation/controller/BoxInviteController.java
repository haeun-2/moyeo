package com.mo.moyeo.domain.box.invitation.controller;

import com.mo.moyeo.domain.auth.security.dto.CustomUserDetails;
import com.mo.moyeo.domain.box.invitation.dto.BoxInviteInfoResponse;
import com.mo.moyeo.domain.box.invitation.dto.BoxInviteResponse;
import com.mo.moyeo.domain.box.invitation.dto.BoxJoinResponse;
import com.mo.moyeo.domain.box.invitation.service.BoxInviteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boxes")
@Tag(name = "BoxInviteController", description = "모임 박스 초대 API")
public class BoxInviteController {

    private final BoxInviteService boxInviteService;

    @Operation(summary = "모임 박스 초대 링크 생성", description = "모임 박스 초대 링크를 생성합니다. 링크 유효기간은 1일 입니다.")
    @PostMapping("/{boxId}/invite")
    public ResponseEntity<BoxInviteResponse> createBoxInviteLink(@PathVariable Long boxId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        BoxInviteResponse response = boxInviteService.createBoxInviteLink(boxId, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "초대 링크로 모임 박스 가입", description = "초대 링크로 모임 박스에 가입합니다.")
    @PostMapping("/invite/{code}/join")
    public ResponseEntity<BoxJoinResponse> joinBoxByInviteLink(@PathVariable String code, @AuthenticationPrincipal CustomUserDetails userDetails) {
        BoxJoinResponse response = boxInviteService.joinBoxByInviteLink(code, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "초대 링크 정보 조회", description = "초대 링크의 정보를 조회합니다. 모임 박스 이름과 유효기간이 반화됩니다.")
    @GetMapping("/invite/{code}/info")
    public ResponseEntity<BoxInviteInfoResponse> getInviteInfo(@PathVariable String code) {
        BoxInviteInfoResponse response = boxInviteService.getInviteInfo(code);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "앱 설치 페이지로 이동", description = "앱 미설치 시 실행되는 fallback 페이지입니다. 앱 설치 페이지로 리다이렉트 합니다.")
    @GetMapping("/invite/{code}")
    public void toFallback(HttpServletResponse response) throws IOException {
        response.sendRedirect("/download.html");
    }

}
