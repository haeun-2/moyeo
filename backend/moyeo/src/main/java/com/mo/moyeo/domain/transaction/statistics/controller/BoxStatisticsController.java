package com.mo.moyeo.domain.transaction.statistics.controller;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.transaction.statistics.dto.BoxStatisticsResponse;
import com.mo.moyeo.domain.transaction.statistics.service.BoxStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boxes/{boxId}/stats")
public class BoxStatisticsController {

    private final BoxStatisticsService boxStatisticsService;

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


}
