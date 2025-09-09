package com.mo.moyeo.domain.box.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class BoxInviteInfoResponse {

    private String boxName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;

    private boolean isValid;

    public static BoxInviteInfoResponse from(BoxInviteDto dto) {
        if (dto == null) {
            return BoxInviteInfoResponse.builder().isValid(false).build();
        }

        return BoxInviteInfoResponse.builder()
                .boxName(dto.getBoxName())
                .expiresAt(dto.getExpiresAt())
                .isValid(dto.getExpiresAt() != null && dto.getExpiresAt().isAfter(LocalDateTime.now()))
                .build();
    }

}
