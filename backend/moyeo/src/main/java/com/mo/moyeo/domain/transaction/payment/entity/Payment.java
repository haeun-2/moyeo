package com.mo.moyeo.domain.transaction.payment.entity;

import com.mo.moyeo.domain.merchant.entity.Merchant;
import com.mo.moyeo.domain.transaction.transaction.entity.Transaction;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "payments")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.PENDING;

    @Column(name = "approval_number", length = 50)
    private String approvalNumber;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "amount")
    private BigDecimal amount;

    public enum Status {
        PENDING,
        APPROVED,
        DECLINED,
        CANCELLED
    }

    public void paymentSuccess(){
        this.status = Status.APPROVED;
    }

    public void paymentFailed(){
        this.status = Status.CANCELLED;
    }

    public void updateCompletedAt(){
        this.completedAt = LocalDateTime.now();
    }
}