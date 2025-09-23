package com.mo.moyeo.domain.exchange.volume.entity;

import com.mo.moyeo.domain.currency.entity.Currency;
import com.mo.moyeo.domain.currency.service.CurrencyService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_volume")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeVolume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_code")
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name="type")
    @Enumerated(EnumType.STRING)
    private Type type;

    public enum Type{
        m,
        h,
        d;
    }
}
