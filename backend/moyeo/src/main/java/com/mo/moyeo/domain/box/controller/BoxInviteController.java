package com.mo.moyeo.domain.box.controller;

import com.mo.moyeo.domain.box.dto.BoxInviteInfoResponse;
import com.mo.moyeo.domain.box.dto.BoxInviteResponse;
import com.mo.moyeo.domain.box.dto.BoxJoinResponse;
import com.mo.moyeo.domain.box.service.BoxInviteService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boxes")
public class BoxInviteController {

    private final BoxInviteService boxInviteService;

    // 모입 박스 초대 링크 생성
    @PostMapping("/{boxId}/invite")
    public ResponseEntity<BoxInviteResponse> createBoxInviteLink(@PathVariable Long boxId) {
        BoxInviteResponse response = boxInviteService.createBoxInviteLink(boxId);
        return ResponseEntity.ok(response);
    }

    // 초대 링크로 모임 박스에 가입
    @PostMapping("/invite/{code}/join")
    public ResponseEntity<BoxJoinResponse> joinBoxByInviteLink(@PathVariable String code) {
        BoxJoinResponse response = boxInviteService.joinBoxByInviteLink(code);
        return ResponseEntity.ok(response);
    }

    // 모임 박스 초대 정보 조회
    @GetMapping("/invite/{code}/info")
    public ResponseEntity<BoxInviteInfoResponse> getInviteInfo(@PathVariable String code) {
        BoxInviteInfoResponse response = boxInviteService.getInviteInfo(code);
        return ResponseEntity.ok(response);
    }

    // 웹 fallback 페이지로 리다이렉트 (앱 미설치 시)
    @GetMapping("/invite/{code}")
    public void toFallback(HttpServletResponse response) throws IOException {
        response.sendRedirect("/download.html");
    }

}
