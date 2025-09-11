package com.mo.moyeo.domain.transaction.history.controller;

import com.mo.moyeo.common.paging.PageResponse;
import com.mo.moyeo.domain.transaction.history.dto.TransactionUpdateRequest;
import com.mo.moyeo.domain.transaction.history.dto.TransactionSearchCondition;
import com.mo.moyeo.domain.transaction.history.dto.TransactionResponse;
import com.mo.moyeo.domain.transaction.history.service.BoxHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boxes/{boxId}/transactions")
@Tag(name = "BoxHistoryController", description = "박스 거래내역 조회 및 수정 API")
public class BoxHistoryController {

    private final BoxHistoryService boxHistoryService;

    @Operation(summary = "박스 거래내역 조회", description = "박스 거래내역을 조회합니다. 페이징 및 검색 조건을 설정할 수 있습니다.")
    @GetMapping
    public ResponseEntity<PageResponse<TransactionResponse>> getTransactions(
            @PathVariable Long boxId,
            @RequestParam TransactionSearchCondition request
    ) {
        PageResponse<TransactionResponse> response = boxHistoryService.getTransactions(boxId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "박스 거래내역 수정", description = "특정 거래내역의 메모, 카테고리를 수정합니다.")
    @PatchMapping("/{transactionId}")
    public void updateTransactionCategory(
            @PathVariable Long transactionId,
            @Valid @RequestBody TransactionUpdateRequest request
    ) {
        boxHistoryService.updateTransactionCategory(transactionId, request);
    }

}
