package com.mo.moyeo.domain.box.controller;

import com.mo.moyeo.common.paging.PageResponse;
import com.mo.moyeo.domain.auth.security.dto.CustomUserDetails;
import com.mo.moyeo.domain.box.dto.BoxCreateRequest;
import com.mo.moyeo.domain.box.dto.BoxCreateResponse;
import com.mo.moyeo.domain.box.dto.BoxPermissionResponse;
import com.mo.moyeo.domain.box.dto.BoxResponse;
import com.mo.moyeo.domain.box.service.BoxService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boxes")
@Tag(name = "BoxController", description = "박스 조회 및 생성 API")
public class BoxController {

    private final BoxService boxService;

    @Operation(summary = "개인 박스 조회", description = "유저의 개인 박스를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<BoxResponse> getMyBox(@AuthenticationPrincipal CustomUserDetails userDetails) {
        BoxResponse response = boxService.getMyBox(userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모임 박스 목록 조회", description = "유저가 가입한 모임 박스 목록을 페이징하여 조회합니다.")
    @GetMapping
    public ResponseEntity<PageResponse<BoxResponse>> getGroupBoxList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size, @AuthenticationPrincipal CustomUserDetails userDetails) {
        PageResponse<BoxResponse> response = boxService.getGroupBoxList(userDetails.getUser(), page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "박스 권한 조회", description = "유저가 가진 박스의 이체/결제/환전 권한을 조회합니다.")
    @GetMapping("/{boxId}/permissions")
    public ResponseEntity<BoxPermissionResponse> getMyBoxPermissions(@PathVariable Long boxId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        BoxPermissionResponse response = boxService.getMyBoxPermissions(boxId, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모임 박스 생성", description = "모임 박스의 기본 이름을 받아서 새로운 모임 박스를 생성합니다. 반화값은 생성된 모임 박스의 ID 입니다.")
    @PostMapping
    public ResponseEntity<BoxCreateResponse> createGroupBox(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody BoxCreateRequest request) {
        BoxCreateResponse response = boxService.createGroupBox(userDetails.getUser(), request);
        return ResponseEntity.ok(response);
    }

}
