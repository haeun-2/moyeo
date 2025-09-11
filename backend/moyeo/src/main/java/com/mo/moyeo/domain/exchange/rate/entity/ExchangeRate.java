package com.mo.moyeo.domain.exchange.rate.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rate_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_code")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Currency currency;

    @Column(name = "buy_rate")
    private Double buyRate;

    @Column(name = "sell_rate")
    private Double sellRate;

    @Column(name = "original_rate")
    private Double originalRate;

    @CreationTimestamp
    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;
}
