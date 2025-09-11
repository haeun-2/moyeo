package com.mo.moyeo.domain.transaction.transfer.controller;

import com.mo.moyeo.domain.auth.security.dto.CustomUserDetails;
import com.mo.moyeo.domain.transaction.transfer.dto.TransferRequest;
import com.mo.moyeo.domain.transaction.transfer.service.TransferService;
import com.mo.moyeo.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transfer")
@Tag(name = "TransferController", description = "이체 API")
public class TransferController {

    private final TransferService transferService;

    @Operation(summary = "이체", description = "박스에서 박스로 이체합니다.")
    @PostMapping
    public void transfer(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Valid TransferRequest request
    ){
        User user = customUserDetails.getUser();
        transferService.transfer(user, request);
    }

}
