package com.mo.moyeo.domain.transaction.payment.entity;

import com.mo.moyeo.domain.merchant.entity.Merchant;
import com.mo.moyeo.domain.transaction.transaction.entity.Transaction;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "payments")
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

    public enum Status {
        PENDING,
        APPROVED,
        DECLINED,
        CANCELLED
    }

}