package com.mo.moyeo.domain.box.member.dto;

import com.mo.moyeo.domain.box.member.entity.BoxMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BoxPermissionDto {

    private Boolean canTransfer;
    private Boolean canPayment;
    private Boolean canExchange;

    public static BoxPermissionDto from(BoxMember boxMember) {
        return BoxPermissionDto.builder()
                .canTransfer(boxMember.getCanTransfer())
                .canPayment(boxMember.getCanPayment())
                .canExchange(boxMember.getCanExchange())
                .build();
    }

    public static BoxPermissionDto fromAllTrue() {
        return BoxPermissionDto.builder()
                .canTransfer(true)
                .canPayment(true)
                .canExchange(true)
                .build();
    }

}
