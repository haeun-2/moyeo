package com.mo.moyeo.domain.user.entity;

import com.mo.moyeo.common.entity.BaseTimeEntity;
import com.mo.moyeo.domain.auth.signup.dto.SignupCompleteRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "email", length = 200, nullable = false)
    private String email;

    @Column(name = "phone_number", length = 15, nullable = false)
    private String phoneNumber;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "connected_bank_code", length = 3, nullable = false)
    private String connectedBankCode;

    @Column(name = "connected_bank_account", length = 20, nullable = false)
    private String connectedBankAccount;

    @Column(name = "connected_bank_key", length = 255, nullable = false)
    private String connectedBankKey;

    @Column(name = "biometric_enabled")
    @Builder.Default
    private Boolean biometricEnabled = false;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    public enum Role {
        USER,
        ADMIN
    }

    public static User from(SignupCompleteRequest request, String hashedPassword, String hashedBankKey) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(hashedPassword)
                .connectedBankCode(request.getConnectedBankCode())
                .connectedBankAccount(request.getConnectedBankAccount())
                .connectedBankKey(hashedBankKey)
                .biometricEnabled(request.getBiometricEnabled())
                .build();
    }
}
