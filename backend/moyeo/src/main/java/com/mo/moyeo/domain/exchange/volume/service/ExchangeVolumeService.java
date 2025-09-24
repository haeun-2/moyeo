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

    public List<ExchangeVolumeDto> getBuyVolumeStatistics(CurrencyType currencyType, ExchangeVolume.Unit unit) {
        if (unit == null) unit = ExchangeVolume.Unit.m;
        return exchangeRepository.getVolumes(currencyType.name(), unit.name(), ExchangeVolume.Type.sell.name())
                .stream().map(ev->new ExchangeVolumeDto(ev.getRecordedAt(), ev.getAmount()))
                .toList();
    }

    public List<ExchangeVolumeDto> getSellVolumeStatistics(CurrencyType currencyType, ExchangeVolume.Unit unit) {
        if (unit == null) unit = ExchangeVolume.Unit.m;
        return exchangeRepository.getVolumes(currencyType.name(), unit.name(), ExchangeVolume.Type.buy.name())
                .stream().map(ev->new ExchangeVolumeDto(ev.getRecordedAt(), ev.getAmount()))
                .toList();
    }
}
