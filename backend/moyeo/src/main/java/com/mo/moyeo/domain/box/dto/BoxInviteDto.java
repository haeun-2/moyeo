package com.mo.moyeo.domain.box.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoxInviteDto {

    private Long boxId;
    private String boxName;
    private LocalDateTime expiresAt;

}
