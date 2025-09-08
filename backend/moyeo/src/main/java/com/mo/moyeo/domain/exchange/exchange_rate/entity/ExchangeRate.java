package com.mo.moyeo.domain.exchange.exchange_rate.entity;

import com.mo.moyeo.domain.currency.entity.Currency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity(name = "exchange_rates")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ExchangeRate {
    @Id
    @Column(name = "rate_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_code")
    private Currency currency;

    @Column(name = "buy_rate")
    private Long buyRate;

    @Column(name = "sell_rate")
    private Long sellRate;

    @CreationTimestamp
    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;
}
