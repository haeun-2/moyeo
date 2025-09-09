package com.mo.moyeo.domain.box.entity;


import com.mo.moyeo.domain.currency.entity.CurrencyType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "box_id", nullable = false)
    private Box box;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code", nullable = false, insertable = true, updatable = false)
    private CurrencyType currencyCode;

    @Column(name = "balance", nullable = false)
    private Double balance = 0d;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public BoxBalance(Box box, CurrencyType currencyCode) {
        this.box = box;
        this.currencyCode = currencyCode;
    }

    // 금액 증가
    public void increaseBalance(Double amount) {
        this.balance += amount;
    }

    // 금액 감소
    public void decreaseBalance(Double amount) {
        if (this.balance < amount) {
            throw new IllegalStateException("잔액이 부족합니다");
        }
        this.balance -= amount;
    }

}