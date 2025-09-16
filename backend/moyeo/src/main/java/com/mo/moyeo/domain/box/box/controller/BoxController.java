package com.mo.moyeo.domain.box.box.controller;

import com.mo.moyeo.domain.auth.security.dto.CustomUserDetails;
import com.mo.moyeo.domain.box.box.dto.*;
import com.mo.moyeo.domain.box.box.service.BoxApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boxes")
@Tag(name = "BoxController", description = "박스 조회 및 생성 API")
public class BoxController {

    private final BoxApplicationService boxApplicationService;

    @Operation(summary = "개인 박스 조회", description = "유저의 개인 박스를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<MyBoxResponse> getMyBox(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MyBoxResponse response = boxApplicationService.getMyBox(userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모임 박스 목록 조회", description = "유저가 가입한 모임 박스 목록을 조회합니다. 북마크한 박스가 우선 조회됩니다.")
    @GetMapping
    public ResponseEntity<List<GroupBoxResponse>> getGroupBoxList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<GroupBoxResponse> response = boxApplicationService.getGroupBoxList(userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "박스 권한 조회", description = "유저가 가진 박스의 이체/결제/환전 권한을 조회합니다.")
    @GetMapping("/{boxId}/permissions")
    public ResponseEntity<BoxPermissionResponse> getMyBoxPermissions(@PathVariable Long boxId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        BoxPermissionResponse response = boxApplicationService.getMyBoxPermissions(boxId, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모임 박스 생성", description = "모임 박스의 기본 이름을 받아서 새로운 모임 박스를 생성합니다. 반화값은 생성된 모임 박스의 ID 입니다.")
    @PostMapping
    public ResponseEntity<BoxCreateResponse> createGroupBox(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody BoxCreateRequest request) {
        BoxCreateResponse response = boxApplicationService.createGroupBox(userDetails.getUser(), request);
        return ResponseEntity.ok(response);
    }

}
