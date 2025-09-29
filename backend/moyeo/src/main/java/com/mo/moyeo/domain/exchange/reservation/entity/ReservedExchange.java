package com.mo.moyeo.domain.exchange.reservation.entity;

import com.mo.moyeo.domain.box.box.entity.Box;
import com.mo.moyeo.domain.currency.entity.Currency;
import com.mo.moyeo.domain.transaction.transaction.entity.Transaction;
import com.mo.moyeo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reserved_exchanges")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ReservedExchange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserved_exchange_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "box_id")
    private Box box;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_currency")
    private Currency fromCurrency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_currency")
    private Currency toCurrency;

    @Column(name = "target_rate")
    private BigDecimal targetRate;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "expires_at")
    private LocalDate expiresAt;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private Status status = Status.WAITING;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    public void cancelReservation() {
        this.status = Status.CANCELLED;
    }

    public void completeReservation() {
        this.status = Status.COMPLETED;
    }

    public void expire() {
        this.status = Status.EXPIRED;
    }

    public enum Status{
        CANCELLED,
        COMPLETED,
        WAITING,
        EXPIRED;
    }



}
