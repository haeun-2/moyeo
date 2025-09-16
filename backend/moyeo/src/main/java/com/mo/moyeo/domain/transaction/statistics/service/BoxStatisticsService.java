package com.mo.moyeo.domain.transaction.statistics.service;

import com.mo.moyeo.domain.box.box.entity.Box;
import com.mo.moyeo.domain.box.box.service.BoxService;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.merchant.entity.Merchant;
import com.mo.moyeo.domain.merchant.repository.MerchantRepository;
import com.mo.moyeo.domain.transaction.history.repository.BoxHistoryRepository;
import com.mo.moyeo.domain.transaction.statistics.dto.MerchantLocationResponse;
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

    private final BoxService boxService;
    private final MerchantRepository merchantRepository;
    private final BoxHistoryRepository boxHistoryRepository;

    public BoxStatisticsResponse getCategoryStatistics(Long boxId, LocalDate startDate, LocalDate endDate, CurrencyType currency) {
        Box box = boxService.getBoxById(boxId);
        if (startDate == null) {
            startDate = box.getCreatedAt().toLocalDate();
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        List<CategoryStatisticsDto> dtoList = boxHistoryRepository.findCategoryStatistics(boxId, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), currency);
        return BoxStatisticsResponse.from(dtoList);
    }

    public List<MerchantLocationResponse> getPaidMerchantLocation(Long boxId) {
        List<Merchant> merchantList = merchantRepository.findPaidMerchantsByBoxId(boxId);
        return MerchantLocationResponse.from(merchantList);
    }

}
