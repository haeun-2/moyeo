package com.mo.moyeo.domain.merchant.entity;

import com.mo.moyeo.common.entity.BaseTimeEntity;
import com.mo.moyeo.domain.transaction.category.entity.Category;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "merchants")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Merchant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "merchant_id")
    private Long id;

    @Column(name = "merchant_name", nullable = false, length = 100)
    private String name;

    @Column(name = "business_number", nullable = false, length = 45)
    private String businessNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "settlement_account", length = 50)
    private String settlementAccount;

    @Column(name = "location_lat")
    private Double locationLat;

    @Column(name = "location_lng")
    private Double locationLng;

}
