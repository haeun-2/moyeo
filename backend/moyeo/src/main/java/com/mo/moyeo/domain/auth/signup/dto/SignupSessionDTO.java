package com.mo.moyeo.domain.auth.signup.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class SignupSessionDTO {

    private String id;
    private Boolean emailVerified = false;
    private Boolean phoneNumberVerified = false;
    private Boolean bankAccountVerified = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public SignupSessionDTO(String id) {
        this.id = id;
    }
}

