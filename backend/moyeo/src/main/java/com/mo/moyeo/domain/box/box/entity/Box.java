package com.mo.moyeo.domain.box.box.entity;

import com.mo.moyeo.common.entity.BaseTimeEntity;
import com.mo.moyeo.domain.box.balance.entity.BoxBalance;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.Arrays;
import java.util.List;

@Getter
@Entity
@Table(name = "boxes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Box extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "box_id")
    private Long id;

    @Column(name = "box_name", length = 100, nullable = false)
    private String boxName;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;

    @OneToMany(mappedBy = "box", cascade = CascadeType.ALL)
    @BatchSize(size = 7)
    private List<BoxBalance> balances;

    @Builder
    public Box(String boxName, Long ownerId, Type type) {
        this.boxName = boxName;
        this.ownerId = ownerId;
        this.type = type;
        this.createInitialBalances();
    }
    
    public boolean isPersonal() {
        return this.type.equals(Type.PERSONAL);
    }

    public boolean isOwner(Long userId) {
        return this.ownerId.equals(userId);
    }

    public boolean isPersonalOwner(Long userId) {
        return isPersonal() && isOwner(userId);
    }

    private void createInitialBalances() {
        // 원화 + 외화 박스 잔액 초기화
        this.balances = Arrays.stream(CurrencyType.values())
                .map(type -> new BoxBalance(this, type))
                .toList();
    }

    public enum Type {
        PERSONAL, GROUP
    }

}
