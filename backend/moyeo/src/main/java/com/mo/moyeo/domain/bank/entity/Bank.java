package com.mo.moyeo.domain.bank.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "banks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Bank {

    @Id
    @Column(name = "bank_code", length = 3)
    private String code;

    @Column(name = "bank_name", length = 50, nullable = false)
    private String bankName;

    @Column(name = "logo_img")
    private String logoImg;
}
