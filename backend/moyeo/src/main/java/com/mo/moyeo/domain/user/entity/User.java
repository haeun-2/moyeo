package com.mo.moyeo.domain.user.entity;

import com.mo.moyeo.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "phone_number", length = 15, nullable = false)
    private String phoneNumber;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "connected_bank_code", length = 3, nullable = false)
    private String connectedBankCode;

    @Column(name = "connected_bank_account", length = 20, nullable = false)
    private String connectedBankAccount;

    @Column(name = "biometric_enabled")
    private Boolean biometricEnabled = false;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
}
