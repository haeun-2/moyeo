package com.mo.moyeo.domain.box.entity;


import com.mo.moyeo.domain.currency.entity.Currency;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "box_balances",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_balance", columnNames = {"box_id", "currency_code"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class BoxBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "box_balance_id")
    private Long boxBalanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "box_id", nullable = false)
    private Box box;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_code", referencedColumnName = "currency_code", nullable = false)
    private Currency currency;

    @Column(name = "balance", nullable = false, precision = 20, scale = 4)
    private BigDecimal balance = BigDecimal.ZERO;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public BoxBalance(Box box, Currency currency, BigDecimal balance) {
        this.box = box;
        this.currency = currency;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
    }

    // 금액 증가
    public void increaseBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    // 금액 감소
    public void decreaseBalance(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalStateException("잔액이 부족합니다");
        }
        this.balance = this.balance.subtract(amount);
    }

}