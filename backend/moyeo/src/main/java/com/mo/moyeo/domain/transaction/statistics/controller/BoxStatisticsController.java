package com.mo.moyeo.domain.transaction.statistics.controller;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.transaction.statistics.dto.MerchantLocationResponse;
import com.mo.moyeo.domain.transaction.statistics.dto.BoxStatisticsResponse;
import com.mo.moyeo.domain.transaction.statistics.service.BoxStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boxes/{boxId}/stats")
@Tag(name = "BoxStatisticsController", description = "박스 통계 조회 API")
public class BoxStatisticsController {

    private final BoxStatisticsService boxStatisticsService;

    @Operation(summary = "카테고리별 통계 조회", description = "지출 거래내역을 카테고리별 백분율, 총지출액로 조회합니다.")
    @GetMapping("/categories")
    public ResponseEntity<BoxStatisticsResponse> getCategoryStatistics(
            @PathVariable Long boxId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam CurrencyType currency
    ) {
        BoxStatisticsResponse response = boxStatisticsService.getCategoryStatistics(boxId, startDate, endDate, currency);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "결제 가맹점 위치 조회", description = "박스에서 결제한 가맹점을 지도에 표시하기 위한 API입니다. 가맹점 이름, 주소, 위경도를 반환합니다.")
    @GetMapping("/map")
    public ResponseEntity<List<MerchantLocationResponse>> getPaidMerchantLocation(@PathVariable Long boxId) {
        List<MerchantLocationResponse> response = boxStatisticsService.getPaidMerchantLocation(boxId);
        return ResponseEntity.ok(response);
    }


}
