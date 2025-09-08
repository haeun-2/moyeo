package com.mo.moyeo.domain.currency.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "currencies")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Currency {
    @Id
    @Column(name = "currency_code")
    @Enumerated(EnumType.STRING)
    private CurrencyType id;

    @Column(name = "country_name")
    private String countryName;

    @Column(name="currency_unit")
    private String currencyUnit;

    @Column(name = "country_flag")
    private String countryFlag;
}
