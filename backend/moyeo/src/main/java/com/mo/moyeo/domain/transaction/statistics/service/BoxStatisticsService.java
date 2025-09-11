package com.mo.moyeo.domain.transaction.statistics.service;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.transaction.history.repository.BoxHistoryRepository;
import com.mo.moyeo.domain.transaction.statistics.dto.BoxStatisticsResponse;
import com.mo.moyeo.domain.transaction.statistics.dto.CategoryStatisticsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoxStatisticsService {

    private final BoxHistoryRepository boxHistoryRepository;

    public BoxStatisticsResponse getCategoryStatistics(Long boxId, LocalDate startDate, LocalDate endDate, CurrencyType currency) {
        List<CategoryStatisticsDto> dtoList = boxHistoryRepository.findCategoryStatistics(boxId, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), currency);
        return BoxStatisticsResponse.from(dtoList);
    }

}
