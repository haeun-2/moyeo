package com.mo.moyeo.domain.transaction.bank.entity;

import com.mo.moyeo.domain.bank.entity.Bank;
import com.mo.moyeo.domain.transaction.transaction.entity.Transaction;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "bank_transactions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BankTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bank_transaction_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bank_code", nullable = false)
    private Bank bank;

    @Column(name = "bank_account", length = 20, nullable = false)
    private String bankAccount;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.PENDING;

    @Column(name = "error_code")
    private String errorCode;

    public void updateStatus(Status status) {
        this.status = status;
    }

    public void updateErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public enum Status {
        PENDING("처리중"),
        BALANCE_UPDATED("잔액변경완료"),
        COMPLETED("완료"),
        FAILED("실패"),
        COMPENSATED("보상완료"),
        COMPENSATION_FAILED("보상실패");

        private final String description;

        Status(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    @Builder
    public BankTransaction(Transaction transaction, Bank bank, String bankAccount, Double amount) {
        this.transaction = transaction;
        this.bank = bank;
        this.bankAccount = bankAccount;
        this.amount = amount;
    }
}
