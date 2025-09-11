package com.mo.moyeo.domain.box.controller;

import com.mo.moyeo.domain.auth.security.dto.CustomUserDetails;
import com.mo.moyeo.domain.box.dto.BoxMemberResponse;
import com.mo.moyeo.domain.box.dto.BoxMemberUpdatePermissionRequest;
import com.mo.moyeo.domain.box.service.BoxMemberService;
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
@RequestMapping("/api/boxes/{boxId}/members")
@Tag(name = "BoxMemberController", description = "모임 박스 멤버 관련 API")
public class BoxMemberController {

    private final BoxMemberService boxMemberService;

    @Operation(summary = "모임 박스 회원 조회", description = "모임 박스에 가입한 회원 목록을 조회합니다. 각 회원의 이름과 권한이 반환됩니다.")
    @GetMapping
    public ResponseEntity<List<BoxMemberResponse>> getMemberList(@PathVariable Long boxId) {
        List<BoxMemberResponse> response = boxMemberService.getGroupBoxMemberList(boxId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모임 박스 회원 권한 수정", description = "모임 박스 회원의 이체/결제/환전 권한을 수정합니다. 모임주만 수정할 수 있습니다.")
    @PatchMapping("/permissions")
    public ResponseEntity<Void> updateMemberPermissions(
            @PathVariable Long boxId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid  @RequestBody List<@Valid BoxMemberUpdatePermissionRequest> requests
    ) {
        boxMemberService.updateGroupBoxMemberPermissions(boxId, userDetails.getUser(), requests);
        return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "모임 박스 탈퇴", description = "유저가 특정 모임 박스에서 탈퇴합니다. 모임주는 탈퇴할 수 없습니다.")
    @DeleteMapping("/me")
    public ResponseEntity<Void> leaveGroupBox(@PathVariable Long boxId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boxMemberService.leaveGroupBox(boxId, userDetails.getUser());
        return ResponseEntity.ok().build();
    }

}
