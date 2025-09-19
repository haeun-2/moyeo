package com.mo.moyeo.domain.transaction.history.controller;

import com.mo.moyeo.common.paging.PageResponse;
import com.mo.moyeo.domain.transaction.history.dto.ExchangeTransactionDetailResponse;
import com.mo.moyeo.domain.transaction.history.dto.TransactionResponse;
import com.mo.moyeo.domain.transaction.history.dto.TransactionSearchCondition;
import com.mo.moyeo.domain.transaction.history.dto.TransactionUpdateRequest;
import com.mo.moyeo.domain.transaction.history.service.BoxHistoryApplicationService;
import com.mo.moyeo.domain.transaction.history.service.BoxHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boxes/{boxId}/transactions/histories")
@Tag(name = "BoxHistoryController", description = "박스 거래내역 조회 및 수정 API")
public class BoxHistoryController {

    private final BoxHistoryApplicationService boxHistoryApplicationService;

    @Operation(summary = "박스 거래내역 조회", description = "박스 거래내역을 조회합니다. 페이징 및 검색 조건을 설정할 수 있습니다.")
    @GetMapping
    public ResponseEntity<PageResponse<TransactionResponse>> getTransactions(
            @PathVariable Long boxId,
            @Valid @ModelAttribute TransactionSearchCondition request
    ) {
        PageResponse<TransactionResponse> response = boxHistoryApplicationService.getTransactions(boxId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "박스 환전 거래내역 상세 조회", description = "특정 환전 거래내역를 상세 조회합니다.")
    @GetMapping("/{historyId}")
    public ResponseEntity<List<ExchangeTransactionDetailResponse>> getExchangeTransactionDetail(@PathVariable Long historyId) {
        List<ExchangeTransactionDetailResponse> response = boxHistoryApplicationService.getExchangeTransactionDetail(historyId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "박스 거래내역 수정", description = "특정 거래내역의 메모, 카테고리를 수정합니다.")
    @PatchMapping("/{historyId}")
    public void updateTransaction(
            @PathVariable Long historyId,
            @Valid @RequestBody TransactionUpdateRequest request
    ) {
        boxHistoryApplicationService.updateTransaction(historyId, request);
    }

}
