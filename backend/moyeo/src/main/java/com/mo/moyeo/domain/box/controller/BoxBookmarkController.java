package com.mo.moyeo.domain.box.controller;

import com.mo.moyeo.domain.auth.security.dto.CustomUserDetails;
import com.mo.moyeo.domain.box.dto.GroupBoxResponse;
import com.mo.moyeo.domain.box.dto.MyBoxResponse;
import com.mo.moyeo.domain.box.service.BoxBookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boxes")
@Tag(name = "BoxBookmarkController", description = "모임 박스 북마크 API")
public class BoxBookmarkController {

    private final BoxBookmarkService boxBookmarkService;

    @Operation(summary = "모임 박스 북마크", description = "특정 모임 박스를 북마크 합니다.")
    @PostMapping("/{boxId}/bookmarks")
    public void bookmarkBox(@PathVariable Long boxId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boxBookmarkService.bookmarkBox(boxId, userDetails.getUser());
    }

    @Operation(summary = "모임 박스 북마크 취소", description = "특정 모임 박스를 북마크 취소 합니다.")
    @DeleteMapping("/{boxId}/bookmarks")
    public void removeBookmarkBox(@PathVariable Long boxId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boxBookmarkService.removeBookmarkBox(boxId, userDetails.getUser());
    }

    @Operation(summary = "북마크한 모임 박스 조회", description = "북마크한 모임 박스 목록을 조회합니다.")
    @GetMapping("/bookmarks")
    public ResponseEntity<List<GroupBoxResponse>> getBookmarkBoxList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<GroupBoxResponse> response = boxBookmarkService.getAllBookmarkBoxList(userDetails.getUser());
        return ResponseEntity.ok(response);
    }

}
