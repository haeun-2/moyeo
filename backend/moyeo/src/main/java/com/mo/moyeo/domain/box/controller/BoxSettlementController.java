package com.mo.moyeo.domain.box.controller;

import com.mo.moyeo.domain.auth.security.dto.CustomUserDetails;
import com.mo.moyeo.domain.box.dto.BoxSettleRequest;
import com.mo.moyeo.domain.box.service.BoxSettlementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boxes/{boxId}/settlements")
public class BoxSettlementController {

    private final BoxSettlementService boxSettlementService;

    @PostMapping
    public void settleBox(@PathVariable Long boxId, @AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody List<@Valid BoxSettleRequest> requests) {
        boxSettlementService.settleBox(boxId, userDetails.getUser(), requests);
    }

}
