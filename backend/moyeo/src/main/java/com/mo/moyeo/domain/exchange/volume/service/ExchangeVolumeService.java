package com.mo.moyeo.domain.exchange.volume.service;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.currency.service.CurrencyService;
import com.mo.moyeo.domain.exchange.volume.dto.ExchangeVolumeDto;
import com.mo.moyeo.domain.exchange.volume.entity.ExchangeVolume;
import com.mo.moyeo.domain.exchange.volume.repository.ExchangeVolumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeVolumeService {
    private final ExchangeVolumeRepository exchangeRepository;

    public List<ExchangeVolumeDto> getVolumeStatistics(CurrencyType currencyType, ExchangeVolume.Type unit) {
        if (unit == null) unit = ExchangeVolume.Type.m;
        return exchangeRepository.getVolumes(currencyType.name(), unit.name())
                .stream().map(ev->new ExchangeVolumeDto(ev.getRecordedAt(), ev.getAmount()))
                .toList();
    }
}
