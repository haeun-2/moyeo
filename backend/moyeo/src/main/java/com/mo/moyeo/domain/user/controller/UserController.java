package com.mo.moyeo.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "UserController", description = "유저 관련 기능 제공")
public class UserController {

    @PostMapping("/test")
    @Operation(summary = "테스트", description = "스웨거 테스트용 기능입니다.")
    public ResponseEntity<?> test() {

        return ResponseEntity.ok().build();
    }
}