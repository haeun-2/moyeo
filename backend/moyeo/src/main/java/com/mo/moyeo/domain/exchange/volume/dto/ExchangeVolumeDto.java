package com.mo.moyeo.domain.exchange.volume.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class ExchangeVolumeDto {
    private String period;
    private Double totalAmount;
}
