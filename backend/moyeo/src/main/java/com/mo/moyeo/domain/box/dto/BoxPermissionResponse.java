package com.mo.moyeo.domain.box.dto;

import com.mo.moyeo.domain.box.entity.Box;
import com.mo.moyeo.domain.box.entity.BoxMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class BoxPermissionResponse {

    private Long boxId;
    private Boolean canTransfer;
    private Boolean canPayment;
    private Boolean canExchange;
    private Boolean isOwner;

    public static BoxPermissionResponse from(Box box) {
        return BoxPermissionResponse.builder()
                .boxId(box.getBoxId())
                .canTransfer(true)
                .canPayment(true)
                .canExchange(true)
                .isOwner(true)
                .build();
    }

    public static BoxPermissionResponse from(BoxMember boxMember, boolean isOwner) {
        return BoxPermissionResponse.builder()
                .boxId(boxMember.getBox().getBoxId())
                .canTransfer(boxMember.getCanTransfer())
                .canPayment(boxMember.getCanPayment())
                .canExchange(boxMember.getCanExchange())
                .isOwner(isOwner)
                .build();
    }

}
