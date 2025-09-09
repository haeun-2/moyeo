package com.mo.moyeo.domain.box.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class BoxInviteResponse {

    private String inviteLink;
    private String inviteCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;

    public static BoxInviteResponse from(String url, String token, LocalDateTime expiresAt) {
        return BoxInviteResponse.builder()
                .inviteLink(url + "/" + token)
                .inviteCode(token)
                .expiresAt(expiresAt)
                .build();
    }

}
