package com.mo.moyeo.domain.transaction.exchange.entity;

import com.mo.moyeo.domain.currency.entity.Currency;
import com.mo.moyeo.domain.transaction.transaction.entity.Transaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name="exchange_transactions")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ExchangeTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exchange_transaction_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_currency_code")
    private Currency fromCurrency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_currency_code")
    private Currency toCurrency;

    @Column(name="exchange_rate")
    private Double exchangeRate;

    @Column(name = "from_amount")
    private Double fromAmount;

    @Column(name = "to_amount")
    private Double toAmount;

}
