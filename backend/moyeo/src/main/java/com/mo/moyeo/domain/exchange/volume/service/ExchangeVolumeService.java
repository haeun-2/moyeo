package com.mo.moyeo.domain.exchange.volume.service;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.exchange.volume.dto.ExchangeVolumeDto;
import com.mo.moyeo.domain.exchange.volume.repository.ExchangeVolumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeVolumeService {
    private final ExchangeVolumeRepository exchangeRepository;

    public List<ExchangeVolumeDto> getVolumeStatistics(CurrencyType currencyType, String unit) {
        String timeFormat;
        if ("10m".equals(unit)) {
            timeFormat = "%Y-%m-%d %H:%i";
        } else if ("1h".equals(unit)) {
            timeFormat = "%Y-%m-%d %H:00:00";
        } else if ("1d".equals(unit)) {
            timeFormat = "%Y-%m-%d";
        } else {
            timeFormat = "%Y-%m-%d %H:%i";
        }

        return exchangeRepository.getStatisticsByCurrencyType(currencyType.name(), timeFormat)
                .stream()
                .map(obj -> new ExchangeVolumeDto(
                        obj.getPeriod(),
                        obj.getTotalAmount()
                ))
                .toList();
    }
}
