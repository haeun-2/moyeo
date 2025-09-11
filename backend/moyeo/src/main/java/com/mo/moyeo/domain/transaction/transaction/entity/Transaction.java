package com.mo.moyeo.domain.transaction.transaction.entity;

import com.mo.moyeo.domain.box.entity.Box;
import com.mo.moyeo.domain.transaction.category.entity.Category;
import com.mo.moyeo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "transactions")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_box_id")
    private Box toBox;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_box_id")
    private Box fromBox;

    @Column(name = "transaction_uuid", length = 30, nullable = false, unique = true)
    private String uuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private Type transactionType;

    @Column(name = "memo", length = 255)
    private String memo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public void update(String memo, Category category) {
        if (memo != null) {
            this.memo = memo;
        }
        if (category != null) {
            this.category = category;
        }
    }

    public enum Type {
        EXCHANGE, PAYMENT, DEPOSIT, WITHDRAW, TRANSFER
    }

}

