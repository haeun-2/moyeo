package com.mo.moyeo.domain.box.entity;

import com.mo.moyeo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "box_members",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_box_user", columnNames = {"box_id", "user_id"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class BoxMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "box_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "box_id", nullable = false)
    private Box box;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "can_transfer", nullable = false)
    private Boolean canTransfer = false;

    @Column(name = "can_payment", nullable = false)
    private Boolean canPayment = false;

    @Column(name = "can_exchange", nullable = false)
    private Boolean canExchange = false;

    @Column(name = "joined_at")
    @CreationTimestamp
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.JOINED;

    @Column(name = "is_bookmarked", nullable = false)
    private Boolean isBookmarked = false;

    public BoxMember(Box box, User user) {
        this.box = box;
        this.user = user;
    }

    public BoxMember(Box box, User user, boolean isOwner) {
        this.box = box;
        this.user = user;
        this.canTransfer = isOwner;
        this.canPayment = isOwner;
        this.canExchange = isOwner;
    }

    public void leave() {
        this.leftAt = LocalDateTime.now();
        this.status = Status.LEFT;
    }

    public void join() {
        this.joinedAt = LocalDateTime.now();
        this.status = Status.JOINED;
    }

    public void updatePermissions(Boolean canTransfer, Boolean canPayment, Boolean canExchange) {
        if (canTransfer != null) this.canTransfer = canTransfer;
        if (canPayment != null) this.canPayment = canPayment;
        if (canExchange != null) this.canExchange = canExchange;
    }

    public boolean isMember() {
        return this.status.equals(Status.JOINED);
    }

    public void unbookmark() {
        this.isBookmarked = false;
    }

    public void bookmark() {
        this.isBookmarked = true;
    }

    public enum Status {
        JOINED,
        LEFT
    }

}
