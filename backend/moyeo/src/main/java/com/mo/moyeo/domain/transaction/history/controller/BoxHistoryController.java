package com.mo.moyeo.domain.transaction.history.controller;

import com.mo.moyeo.common.paging.PageResponse;
import com.mo.moyeo.domain.transaction.history.dto.TransactionUpdateRequest;
import com.mo.moyeo.domain.transaction.history.dto.TransactionSearchCondition;
import com.mo.moyeo.domain.transaction.history.dto.TransactionResponse;
import com.mo.moyeo.domain.transaction.history.service.BoxHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boxes/{boxId}/transactions")
public class BoxHistoryController {

    private final BoxHistoryService boxHistoryService;

    @GetMapping
    public ResponseEntity<PageResponse<TransactionResponse>> getTransactions(
            @PathVariable Long boxId,
            @RequestParam TransactionSearchCondition request
    ) {
        PageResponse<TransactionResponse> response = boxHistoryService.getTransactions(boxId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{transactionId}")
    public void updateTransactionCategory(
            @PathVariable Long transactionId,
            @Valid @RequestBody TransactionUpdateRequest request
    ) {
        boxHistoryService.updateTransactionCategory(transactionId, request);
    }

}
