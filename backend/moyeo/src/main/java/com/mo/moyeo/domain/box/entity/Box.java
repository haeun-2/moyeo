package com.mo.moyeo.domain.box.entity;

import com.mo.moyeo.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.List;

@Getter
@Entity
@Table(name = "boxes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Box extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "box_id")
    private Long boxId;

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
    }

    public void updateBoxName(String boxName) {
        this.boxName = boxName;
    }

    public boolean isPersonal() {
        return this.type.equals(Type.PERSONAL);
    }

    public enum Type {
        PERSONAL, GROUP
    }

}
