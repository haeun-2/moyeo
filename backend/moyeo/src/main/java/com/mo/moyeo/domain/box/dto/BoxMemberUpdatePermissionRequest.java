package com.mo.moyeo.domain.box.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoxMemberUpdatePermissionRequest {

        @NotNull(message = "멤버 정보가 필요합니다.")
        private Long boxMemberId;
        private Boolean canTransfer;
        private Boolean canPayment;
        private Boolean canExchange;

}

