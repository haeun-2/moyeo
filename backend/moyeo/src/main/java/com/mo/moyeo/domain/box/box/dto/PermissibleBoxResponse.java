package com.mo.moyeo.domain.box.box.dto;

import com.mo.moyeo.domain.box.box.entity.Box;
import com.mo.moyeo.domain.box.member.entity.BoxMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class PermissibleBoxResponse {

    private Long boxId;
    private String name;
    private List<BalanceDto> balances;
    private String type;
    private Boolean isBookmarked;

    public static PermissibleBoxResponse from(Box box) {
        return PermissibleBoxResponse.builder()
                .boxId(box.getId())
                .name(box.getBoxName())
                .balances(BalanceDto.from(box.getBalances()))
                .type(box.getType().name())
                .build();
    }


    public static PermissibleBoxResponse from(BoxMember boxMember) {
        return PermissibleBoxResponse.builder()
                .boxId(boxMember.getBox().getId())
                .name(boxMember.getBox().getBoxName())
                .balances(BalanceDto.from(boxMember.getBox().getBalances()))
                .type(boxMember.getBox().getType().name())
                .isBookmarked(boxMember.getIsBookmarked())
                .build();
    }

    public static List<PermissibleBoxResponse> from(List<BoxMember> boxMembers) {
        return boxMembers.stream().map(PermissibleBoxResponse::from).toList();
    }

}
